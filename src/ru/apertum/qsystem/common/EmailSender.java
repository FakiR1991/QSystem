/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common;

import ru.apertum.qsystem.common.model.QEmailSendingSettings;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.Spring;

/**
 *
 * @author zaikov
 */
public class EmailSender {
    private final QEmailSendingSettings emailSendingSettings;

    public EmailSender() {
        this.emailSendingSettings = getEmailSendingSettings();
    }
    
    /**
     * Получить настройки для отправки уведомлений по email.
     * @return Настройки для отправки уведомлений.
     */
    private QEmailSendingSettings getEmailSendingSettings() {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        String query = "select id, smtpHost, smtpPort, email, password from QEmailSendingSettings";
        QEmailSendingSettings sendingSettings;
        try {
            sendingSettings = Spring.getInstance().executeSelectEmailSendingSettings(query);
        }
        catch (Exception ex) {
            throw new ServerException("\n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        Spring.getInstance().getTxManager().commit(status);
        
        return sendingSettings;
    }
    
    /**
     * Метод, который отправляет уведомления о большой очереди в системе электронной очереди.
     */
    public void sendMessages(String email, String subject, String messageText) {
        Session session = getSession();

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailSendingSettings.getEmail()));
            message.setRecipients(Message.RecipientType.TO,
                                  InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
        } catch (MessagingException e) {
            QLog.l().logger().error("Ошибка отправки уведомления на e-mail.\n" + e.getMessage());
        }
    }
    
    private Session getSession() {
        return Session.getInstance(initProperties(),
            new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailSendingSettings.getEmail(), emailSendingSettings.getPassword());
                }
            });
    }
    
    private Properties initProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailSendingSettings.getSmtpHost());
        props.put("mail.smtp.port", emailSendingSettings.getSmtpPort());
        return props;
    }
}
