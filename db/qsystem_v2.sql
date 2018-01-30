CREATE DATABASE  IF NOT EXISTS `qsystem` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `qsystem`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: qsystem
-- ------------------------------------------------------
-- Server version	5.7.19-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `advance`
--

DROP TABLE IF EXISTS `advance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `advance` (
  `id` bigint(20) NOT NULL,
  `service_id` bigint(20) NOT NULL COMMENT 'Услуга предварительной записи',
  `advance_time` datetime NOT NULL COMMENT 'Время предварительной записи',
  `priority` int(11) NOT NULL DEFAULT '2' COMMENT 'Приоритет заранее записавшегося клиента.',
  `clients_authorization_id` bigint(20) DEFAULT NULL COMMENT 'Определено если клиент авторизовался',
  `input_data` varchar(150) DEFAULT NULL COMMENT 'Введеные при предвариловке данные клиента если услуга этого требует',
  `comments` varchar(345) DEFAULT '' COMMENT 'Коментарии при записи предварительно оператором удаленно',
  PRIMARY KEY (`id`),
  KEY `idx_scenario_services` (`service_id`),
  KEY `idx_advance_clients_authorization` (`clients_authorization_id`),
  CONSTRAINT `fk_advance_clients_authorization` FOREIGN KEY (`clients_authorization_id`) REFERENCES `clients_authorization` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_scenario_services` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Таблица предварительной записи';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `advance`
--

LOCK TABLES `advance` WRITE;
/*!40000 ALTER TABLE `advance` DISABLE KEYS */;
/*!40000 ALTER TABLE `advance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `break`
--

DROP TABLE IF EXISTS `break`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `break` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `breaks_id` bigint(20) DEFAULT NULL,
  `from_time` time NOT NULL,
  `to_time` time NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_break_breaks1` (`breaks_id`),
  CONSTRAINT `fk_break_breaks1` FOREIGN KEY (`breaks_id`) REFERENCES `breaks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Перерывы в работе для предвариловки';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `break`
--

LOCK TABLES `break` WRITE;
/*!40000 ALTER TABLE `break` DISABLE KEYS */;
/*!40000 ALTER TABLE `break` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `breaks`
--

DROP TABLE IF EXISTS `breaks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `breaks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(245) NOT NULL DEFAULT 'Unknown',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Списки наборов перерывов для привязки к дневному расписанию';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `breaks`
--

LOCK TABLES `breaks` WRITE;
/*!40000 ALTER TABLE `breaks` DISABLE KEYS */;
/*!40000 ALTER TABLE `breaks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calendar`
--

DROP TABLE IF EXISTS `calendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar` (
  `id` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL DEFAULT '' COMMENT 'Название календаря',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Календарь услуг на год';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calendar`
--

LOCK TABLES `calendar` WRITE;
/*!40000 ALTER TABLE `calendar` DISABLE KEYS */;
INSERT INTO `calendar` VALUES (1,'Общий календарь');
/*!40000 ALTER TABLE `calendar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calendar_out_days`
--

DROP TABLE IF EXISTS `calendar_out_days`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar_out_days` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `out_day` date NOT NULL COMMENT 'Дата неработы. Важен месяц и день',
  `calendar_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_calendar_out_days_calendar` (`calendar_id`),
  CONSTRAINT `fk_calendar_out_days_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Дни неработы услуг';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calendar_out_days`
--

LOCK TABLES `calendar_out_days` WRITE;
/*!40000 ALTER TABLE `calendar_out_days` DISABLE KEYS */;
INSERT INTO `calendar_out_days` VALUES (1,'2010-01-01',1);
/*!40000 ALTER TABLE `calendar_out_days` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients` (
  `id` bigint(20) NOT NULL COMMENT 'Первичный ключ.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `service_id` bigint(20) NOT NULL COMMENT 'Услуга, к которой  пришел первоначально кастомер.  Вспомогательное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `user_id` bigint(20) NOT NULL COMMENT ' Вспомогательное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `service_prefix` varchar(45) NOT NULL COMMENT 'Префикс номера кастомера. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `number` int(11) NOT NULL COMMENT 'Номер клиента без префикса. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `stand_time` datetime NOT NULL COMMENT 'Время постановки в очередь. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `start_time` datetime NOT NULL COMMENT 'Время начала обработки клиента пользователем. Вспомогательное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `finish_time` datetime NOT NULL COMMENT 'Время завершения работы с клиентом пользователем. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.',
  `clients_authorization_id` bigint(20) DEFAULT NULL COMMENT 'Определено если клиент авторизовался',
  `result_id` bigint(20) DEFAULT NULL COMMENT 'Если выбрали результат работы',
  `input_data` varchar(150) NOT NULL DEFAULT '' COMMENT 'Введенные данные пользователем',
  `state_in` int(11) NOT NULL DEFAULT '0' COMMENT 'клиент перешел в это состояние.',
  PRIMARY KEY (`id`),
  KEY `idx_сlients_service_id_services_id` (`service_id`),
  KEY `idx_сlients_user_id_users_id` (`user_id`),
  KEY `idx_clients_clients_authorization` (`clients_authorization_id`),
  KEY `idx_clients_results` (`result_id`),
  CONSTRAINT `fk_clients_clients_authorization` FOREIGN KEY (`clients_authorization_id`) REFERENCES `clients_authorization` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_clients_results` FOREIGN KEY (`result_id`) REFERENCES `results` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_сlients_service_id_services_id` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_сlients_user_id_users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Таблица регистрации статистических событий клиентов.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clients_authorization`
--

DROP TABLE IF EXISTS `clients_authorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients_authorization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `auth_id` varchar(128) DEFAULT NULL COMMENT 'Если есть строковый идентификатор, то можно использовать',
  `name` varchar(45) NOT NULL DEFAULT '' COMMENT 'Имя',
  `surname` varchar(45) NOT NULL DEFAULT '' COMMENT 'Фамилие',
  `otchestvo` varchar(45) NOT NULL DEFAULT '' COMMENT 'Отчество, иногда может отсутствовать.',
  `birthday` date DEFAULT NULL COMMENT 'Дата рождения',
  `streets_id` bigint(20) DEFAULT NULL COMMENT 'Связь со словарем улиц. Проживание.',
  `house` varchar(10) DEFAULT '' COMMENT 'Номер дома',
  `korp` varchar(10) DEFAULT '' COMMENT 'Корпус дома',
  `flat` varchar(10) DEFAULT '' COMMENT 'Квартира',
  `validity` int(11) NOT NULL DEFAULT '-1' COMMENT 'Степень валидности авторизованного клиента',
  `comments` varchar(512) DEFAULT NULL COMMENT 'Некий необязательный комментарий',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_auth_id_UNIQUE` (`auth_id`),
  KEY `idx_clients_authorization_streets` (`streets_id`),
  CONSTRAINT `fk_clients_authorization_streets` FOREIGN KEY (`streets_id`) REFERENCES `streets` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Словарь клиентов для авторизации.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients_authorization`
--

LOCK TABLES `clients_authorization` WRITE;
/*!40000 ALTER TABLE `clients_authorization` DISABLE KEYS */;
/*!40000 ALTER TABLE `clients_authorization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `information`
--

DROP TABLE IF EXISTS `information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `information` (
  `id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL COMMENT 'Наименование узла справки',
  `text` text NOT NULL COMMENT 'html-текст справки',
  `text_print` text NOT NULL COMMENT 'Текст для печати информационного узла',
  PRIMARY KEY (`id`),
  KEY `idx_information_information` (`parent_id`),
  CONSTRAINT `fk_information_information` FOREIGN KEY (`parent_id`) REFERENCES `information` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Таблица справочной информации древовидной структуры';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `information`
--

LOCK TABLES `information` WRITE;
/*!40000 ALTER TABLE `information` DISABLE KEYS */;
INSERT INTO `information` VALUES (1,NULL,'Справочная система','<html><p align=center><span style=\'font-size:55.0;color:#DC143C\'>Справочная информация<br><span style=\'font-size:45.0;color:#DC143C\'><i>Прочитайте и распечатайте памятку</i></span></p>','Для  получения детальной информации обратитесь к менеджеру');
/*!40000 ALTER TABLE `information` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `net`
--

DROP TABLE IF EXISTS `net`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `net` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ПортСервера="3128" ПортВебСервера="8080" ПортКлиента="3129" АдресСервера="localhost"',
  `server_port` int(11) NOT NULL COMMENT 'Серверный порт приема заданий по сети от клиетских приложений.',
  `web_server_port` int(11) NOT NULL COMMENT 'Серверный порт для приема web запросов в системе отчетов.',
  `client_port` int(11) NOT NULL COMMENT 'UDP Порт клиента, на который идет рассылка широковещательных пакетов.',
  `finish_time` time NOT NULL COMMENT 'Время прекращения приема заявок на постановку в очередь',
  `start_time` time NOT NULL COMMENT 'Время начала приема заявок на постановку в очередь',
  `version` varchar(25) NOT NULL DEFAULT 'Не присвоена' COMMENT 'Версия БД',
  `first_number` int(11) NOT NULL DEFAULT '1',
  `last_number` int(11) NOT NULL DEFAULT '999',
  `numering` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0 общая нумерация, 1 для каждой услуги своя нумерация',
  `point` int(11) NOT NULL DEFAULT '0' COMMENT '0 кабинет, 1 окно, 2 стойка',
  `sound` int(11) NOT NULL DEFAULT '2' COMMENT '0 нет оповещения, 1 только сигнал, 2 сигнал+голос',
  `branch_id` bigint(20) NOT NULL DEFAULT '-1',
  `sky_server_url` varchar(145) NOT NULL DEFAULT '',
  `zone_board_serv_addr` varchar(145) NOT NULL DEFAULT '',
  `zone_board_serv_port` bigint(20) NOT NULL DEFAULT '0',
  `voice` int(11) NOT NULL DEFAULT '0' COMMENT '0 - по умолчанию, ну и т.д. по набору звуков',
  `black_time` int(11) NOT NULL DEFAULT '0' COMMENT 'Время нахождения в блеклисте в минутах. 0 - попавшие в блекслист не блокируются',
  `limit_recall` int(11) NOT NULL DEFAULT '0' COMMENT 'Количество повторных вызовов перед отклонением неявившегося посетителя, 0-бесконечно',
  `button_free_design` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'авторасстановка кнопок на киоске',
  `ext_priority` int(11) NOT NULL DEFAULT '0' COMMENT 'Количество дополнительных приоритетов',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Сетевые настройки сервера.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `net`
--

LOCK TABLES `net` WRITE;
/*!40000 ALTER TABLE `net` DISABLE KEYS */;
INSERT INTO `net` VALUES (1,3128,8088,3129,'18:00:00','08:45:00','5',1,999,0,0,1,113,'http://localhost:8080/qskyapi/customer_events?wsdl','127.0.0.1',27007,0,0,0,0,0);
/*!40000 ALTER TABLE `net` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `properties`
--

DROP TABLE IF EXISTS `properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `properties` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hide` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Показывать пользователю этот параметр или нет',
  `psection` varchar(128) DEFAULT NULL COMMENT 'Раздел',
  `pkey` varchar(128) NOT NULL COMMENT 'Ключ',
  `pvalue` varchar(10240) DEFAULT NULL COMMENT 'Значение',
  `pcomment` varchar(256) DEFAULT NULL,
  `pdata` text COMMENT 'Не типизированные текстовые данные.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `section_key_idx` (`psection`,`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Системные настройки и параметры';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `properties`
--

LOCK TABLES `properties` WRITE;
/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;
UNLOCK TABLES;

DELIMITER $$

USE `qsystem`$$
DROP TRIGGER IF EXISTS `qsystem`.`insert_to_user_stat` $$
DROP TRIGGER IF EXISTS `qsystem`.`update_to_user_stat` $$
USE `qsystem`$$

CREATE TRIGGER insert_to_user_stat AFTER INSERT ON clients
FOR EACH ROW 
BEGIN 
INSERT INTO users_statistic(user_id, dt,  operation_id, dt_start, dt_stop, service_id, state_in, client_id)
VALUES
  (new.user_id, new.finish_time,2, new.start_time, new.finish_time, new.service_id,  new.state_in, new.id);
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER update_to_user_stat AFTER UPDATE ON clients
FOR EACH ROW 
BEGIN 
INSERT INTO users_statistic(user_id, dt,  operation_id, dt_start, dt_stop, service_id, state_in, client_id)
VALUES
  (new.user_id, new.finish_time,2, new.start_time, new.finish_time, new.service_id,  new.state_in, new.id);
END $$
DELIMITER ;

DELIMITER $$

USE `qsystem`$$
DROP TRIGGER IF EXISTS `qsystem`.`insert_to_statistic` $$
USE `qsystem`$$



CREATE TRIGGER insert_to_statistic 
    AFTER INSERT ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period) 
    VALUES
        (NEW.state_in, NEW.result_id, NEW.user_id, NEW.id, NEW.service_id, NEW.start_time, NEW.finish_time, NEW.stand_time, 
        round(
                (HOUR(@finish_start) * 60 * 60 +
                 MINUTE(@finish_start) * 60 +
                 SECOND(@finish_start) + 59)/60),
        round(
                (HOUR(@start_starnd) * 60 * 60 +
                MINUTE(@start_starnd) * 60 +
                SECOND(@start_starnd) + 59)/60)  
        );
END;$$


USE `qsystem`$$
DROP TRIGGER IF EXISTS `qsystem`.`update_to_statistic` $$
USE `qsystem`$$



CREATE TRIGGER update_to_statistic
    AFTER UPDATE ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period) 
    VALUES
        (NEW.state_in, NEW.result_id, NEW.user_id, NEW.id, NEW.service_id, NEW.start_time, NEW.finish_time, NEW.stand_time, 
        round(
                (HOUR(@finish_start) * 60 * 60 +
                 MINUTE(@finish_start) * 60 +
                 SECOND(@finish_start) + 59)/60),
        round(
                (HOUR(@start_starnd) * 60 * 60 +
                MINUTE(@start_starnd) * 60 +
                SECOND(@start_starnd) + 59)/60)  
        );
END;$$


DELIMITER ;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reports` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT 'Название отчета, выводимое пользователю.',
  `className` varchar(150) NOT NULL COMMENT 'Класс формирования отчета. Полное наименование класса с пакетами.',
  `template` varchar(150) NOT NULL COMMENT 'Шаблон отчета. Хранится в отдельном пакете в jar.',
  `href` varchar(150) NOT NULL COMMENT 'Ссылка на отчет в index.html. Без расширения типа файла.',
  PRIMARY KEY (`id`,`href`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='Зарегистрированные аналитические отчеты.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (1,'Статистический отчет в разрезе услуг за период','ru.apertum.qsystem.reports.formirovators.StatisticServices','/ru/apertum/qsystem/reports/templates/statisticServicesPeriod.jasper','statistic_period_services'),(2,'Статистический отчет в разрезе персонала за период','ru.apertum.qsystem.reports.formirovators.StatisticUsers','/ru/apertum/qsystem/reports/templates/statisticUsersPeriod.jasper','statistic_period_users'),(3,'Отчет по распределению клиентов по виду услуг за период','ru.apertum.qsystem.reports.formirovators.RatioServices','/ru/apertum/qsystem/reports/templates/ratioServicesPeriod.jasper','ratio_period_services'),(4,'Распределение нагрузки внутри дня','ru.apertum.qsystem.reports.formirovators.DistributionJobDay','/ru/apertum/qsystem/reports/templates/DistributionJobDay.jasper','distribution_job_day'),(5,'Распределение нагрузки внутри дня для услуги','ru.apertum.qsystem.reports.formirovators.DistributionJobDayServices','/ru/apertum/qsystem/reports/templates/DistributionJobDayServices.jasper','distribution_job_services'),(6,'Распределение нагрузки внутри дня для пользователя','ru.apertum.qsystem.reports.formirovators.DistributionJobDayUsers','/ru/apertum/qsystem/reports/templates/DistributionJobDayUsers.jasper','distribution_job_users'),(7,'Распределение среднего времени ожидания внутри дня','ru.apertum.qsystem.reports.formirovators.DistributionWaitDay','/ru/apertum/qsystem/reports/templates/DistributionWaitDay.jasper','distribution_wait_day'),(8,'Распределение среднего времени ожидания внутри дня для услуги','ru.apertum.qsystem.reports.formirovators.DistributionWaitDayServices','/ru/apertum/qsystem/reports/templates/DistributionWaitDayServices.jasper','distribution_wait_services'),(9,'Распределение среднего времени ожидания внутри дня для пользователя','ru.apertum.qsystem.reports.formirovators.DistributionWaitDayUsers','/ru/apertum/qsystem/reports/templates/DistributionWaitDayUsers.jasper','distribution_wait_users'),(10,'Статистический отчет по отзывам клиентов за период','ru.apertum.qsystem.reports.formirovators.ResponsesReport','/ru/apertum/qsystem/reports/templates/responsesReport.jasper','statistic_period_responses'),(11,'Полный отчет по отзывам клиентов за период','ru.apertum.qsystem.reports.formirovators.ResponsesDateReport','/ru/apertum/qsystem/reports/templates/responsesDateReport.jasper','statistic_period_date_responses'),(12,'Отчет по предварительно зарегистрированным клиентам на дату','ru.apertum.qsystem.reports.formirovators.DistributionMedDayServices','/ru/apertum/qsystem/reports/templates/DistributionMedDayServices.jasper','distribution_med_services'),(13,'Отчет по авторизованным персонам за период для пользователя','ru.apertum.qsystem.reports.formirovators.AuthorizedClientsPeriodUsers','/ru/apertum/qsystem/reports/templates/AuthorizedClientsPeriodUsers.jasper','authorized_clients_period_users'),(14,'Отчет по авторизованным персонам за период для услуги','ru.apertum.qsystem.reports.formirovators.AuthorizedClientsPeriodServices','/ru/apertum/qsystem/reports/templates/AuthorizedClientsPeriodServices.jasper','authorized_clients_period_services'),(15,'Отчет по результатам работы за период в разрезе услуг','ru.apertum.qsystem.reports.formirovators.ResultStateServices','/ru/apertum/qsystem/reports/templates/resultStateServicesPeriod.jasper','result_state_services');
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `response_event`
--

DROP TABLE IF EXISTS `response_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resp_date` datetime NOT NULL COMMENT 'Дата отклика',
  `response_id` bigint(20) NOT NULL,
  `services_id` bigint(20) DEFAULT NULL,
  `users_id` bigint(20) DEFAULT NULL,
  `clients_id` bigint(20) DEFAULT NULL COMMENT 'Клиент оставивший отзыв',
  `client_data` varchar(245) NOT NULL DEFAULT '',
  `comment` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_response_date_responses` (`response_id`),
  KEY `idx_response_event_services` (`services_id`),
  KEY `idx_response_event_users` (`users_id`),
  KEY `idx_response_event_clients` (`clients_id`),
  CONSTRAINT `fk_response_date_responses` FOREIGN KEY (`response_id`) REFERENCES `responses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_response_event_clients` FOREIGN KEY (`clients_id`) REFERENCES `clients` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_response_event_services` FOREIGN KEY (`services_id`) REFERENCES `services` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_response_event_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Даты оставленных отзывов.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `response_event`
--

LOCK TABLES `response_event` WRITE;
/*!40000 ALTER TABLE `response_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `response_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `responses`
--

DROP TABLE IF EXISTS `responses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `responses` (
  `id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL DEFAULT '',
  `text` varchar(5000) NOT NULL DEFAULT '',
  `input_caption` varchar(512) NOT NULL DEFAULT '',
  `input_required` tinyint(1) NOT NULL DEFAULT '0',
  `deleted` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_responses_responses` (`parent_id`),
  CONSTRAINT `fk_responses_responses` FOREIGN KEY (`parent_id`) REFERENCES `responses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Список отзывов в отратной связи';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `responses`
--

LOCK TABLES `responses` WRITE;
/*!40000 ALTER TABLE `responses` DISABLE KEYS */;
INSERT INTO `responses` VALUES (0,NULL,'Отзывы посетителей','<html><p  style=\"text-align: center;\"><font size=\"10\" color=\"#ffff4d\">Помогите нам улучшить нашу работую</font><br><font  size=\"4\" color=\"#ffff4d\">Каждый ваш отзыв очень важен для нас.</font></p>','',0,NULL),(1,0,'Отлично','<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Отлично</span></b>','',0,NULL),(2,0,'Хорошо','<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Хорошо</span></b>','',0,NULL),(3,0,'Удовлетворительно','<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Удовлетворительно</span></b>','',0,NULL),(4,0,'Плохо','<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Плохо</span></b>','',0,NULL),(5,0,'Отвратительно','<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Отвратительно</span></b>','',0,NULL);
/*!40000 ALTER TABLE `responses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `results`
--

DROP TABLE IF EXISTS `results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `results` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL DEFAULT '' COMMENT 'Текст результата',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='Справочник результатов работы с клиентом';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `results`
--

LOCK TABLES `results` WRITE;
/*!40000 ALTER TABLE `results` DISABLE KEYS */;
INSERT INTO `results` VALUES (1,'Обращение отработано'),(2,'Невозможно отработать');
/*!40000 ALTER TABLE `results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schedule` (
  `id` bigint(20) NOT NULL,
  `name` varchar(150) NOT NULL DEFAULT '' COMMENT 'Наименование плана',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT 'Тип плана\n0 - недельный\n1 - четные/нечетные дни',
  `time_begin_1` time DEFAULT NULL COMMENT 'Время начала работы',
  `time_end_1` time DEFAULT NULL COMMENT 'Время завершения работы',
  `time_begin_2` time DEFAULT NULL,
  `time_end_2` time DEFAULT NULL,
  `time_begin_3` time DEFAULT NULL,
  `time_end_3` time DEFAULT NULL,
  `time_begin_4` time DEFAULT NULL,
  `time_end_4` time DEFAULT NULL,
  `time_begin_5` time DEFAULT NULL,
  `time_end_5` time DEFAULT NULL,
  `time_begin_6` time DEFAULT NULL,
  `time_end_6` time DEFAULT NULL,
  `time_begin_7` time DEFAULT NULL,
  `time_end_7` time DEFAULT NULL,
  `breaks_id1` bigint(20) DEFAULT NULL,
  `breaks_id2` bigint(20) DEFAULT NULL,
  `breaks_id3` bigint(20) DEFAULT NULL,
  `breaks_id4` bigint(20) DEFAULT NULL,
  `breaks_id5` bigint(20) DEFAULT NULL,
  `breaks_id6` bigint(20) DEFAULT NULL,
  `breaks_id7` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_schedule_breaks1` (`breaks_id1`),
  KEY `idx_schedule_breaks2` (`breaks_id2`),
  KEY `idx_schedule_breaks3` (`breaks_id7`),
  KEY `idx_schedule_breaks4` (`breaks_id3`),
  KEY `idx_schedule_breaks5` (`breaks_id4`),
  KEY `idx_schedule_breaks6` (`breaks_id5`),
  KEY `idx_schedule_breaks7` (`breaks_id6`),
  CONSTRAINT `fk_schedule_breaks1` FOREIGN KEY (`breaks_id1`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks2` FOREIGN KEY (`breaks_id2`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks3` FOREIGN KEY (`breaks_id7`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks4` FOREIGN KEY (`breaks_id3`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks5` FOREIGN KEY (`breaks_id4`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks6` FOREIGN KEY (`breaks_id5`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks7` FOREIGN KEY (`breaks_id6`) REFERENCES `breaks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Справочник расписаний для услуг';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` VALUES (1,'План работы с 8.00 до 17.00',0,'08:00:00','17:00:00','08:00:00','17:00:00','08:00:00','17:00:00','08:00:00','17:00:00','08:00:00','17:00:00','08:00:00','17:00:00','08:00:00','17:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'План работы по четным/нечетным',1,'08:00:00','13:00:00','12:00:00','17:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services` (
  `id` bigint(20) NOT NULL,
  `name` varchar(2000) NOT NULL COMMENT 'Наименование услуги',
  `description` varchar(2000) DEFAULT NULL COMMENT 'Описание услуги.',
  `service_prefix` varchar(10) DEFAULT '',
  `button_text` varchar(2500) NOT NULL DEFAULT '' COMMENT 'HTML-текст для вывода на кнопки регистрации.',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT 'Состояние услуги. 1 - доступна, 0 - недоступна, -1 - невидима. 2 - только предварительная запись, 3-заглушка',
  `enable` int(11) NOT NULL DEFAULT '1' COMMENT 'Способ вызова клиента юзером\n1 - стандартно\n2 - backoffice, т.е. вызов следующего без табло и звука, запершение только редиректом',
  `prent_id` bigint(20) DEFAULT NULL COMMENT 'Групповое подчинение.',
  `day_limit` int(11) NOT NULL DEFAULT '0' COMMENT 'ограничение выданных билетов в день. 0-нет ограничения',
  `person_day_limit` int(11) NOT NULL DEFAULT '0' COMMENT 'ограничение выданных билетов в день клиентам с одинаковыми введенными данными. 0-нет ограничения',
  `advance_limit` int(11) NOT NULL DEFAULT '1' COMMENT 'Ограничение по количеству предварительно регистрировшихся в час',
  `advance_limit_period` int(11) DEFAULT '14' COMMENT 'ограничение в днях, в пределах которого можно записаться вперед. может быть null или 0 если нет ограничения',
  `advance_time_period` int(11) NOT NULL DEFAULT '60' COMMENT 'периоды, на которые делится день, для записи предварительно',
  `schedule_id` bigint(20) DEFAULT NULL COMMENT 'План работы услуги',
  `input_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Обязывать кастомера вводить что-то перед постоновкой в очередь',
  `input_caption` varchar(2000) NOT NULL DEFAULT 'Введите номер документа' COMMENT 'Текст над полем ввода обязательного ввода',
  `inputed_as_number` int(11) NOT NULL DEFAULT '0' COMMENT 'Если требуется использовать введенные пользователем данные как его номер в очереди. 0 - генерировать как обычно',
  `inputed_as_ext` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Разрешение выводить введенные данные в третью колонку на табло и в панель вызова',
  `result_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Требовать ввод пользователем результата работы с клиентом',
  `calendar_id` bigint(20) DEFAULT NULL,
  `pre_info_html` text NOT NULL COMMENT 'html текст информационного сообщения перед постановкой в очередь',
  `pre_info_print_text` text NOT NULL COMMENT 'текст для печати при необходимости перед постановкой в очередь',
  `point` int(11) NOT NULL DEFAULT '0' COMMENT 'указание для какого пункта регистрации услуга, 0-для всех, х-для киоска х.',
  `ticket_text` varchar(1500) DEFAULT NULL COMMENT 'Текст напечатается на талоне.',
  `tablo_text` varchar(1500) NOT NULL DEFAULT '' COMMENT 'Текст для вывода на главное табло в шаблоны панели вызванного и третью колонку пользователя',
  `seq_id` int(11) NOT NULL DEFAULT '0' COMMENT 'порядок следования кнопок услуг на пункте регистрации',
  `but_x` int(11) NOT NULL DEFAULT '0' COMMENT 'позиция кнопки',
  `but_y` int(11) NOT NULL DEFAULT '0' COMMENT 'позиция кнопки',
  `but_b` int(11) NOT NULL DEFAULT '0' COMMENT 'позиция кнопки',
  `but_h` int(11) NOT NULL DEFAULT '0' COMMENT 'позиция кнопки',
  `deleted` date DEFAULT NULL COMMENT 'признак удаления с проставленим даты',
  `duration` int(11) NOT NULL DEFAULT '1' COMMENT 'Норматив. Среднее время оказания этой услуги.  Пока для маршрутизации при медосмотре',
  `sound_template` varchar(45) DEFAULT NULL COMMENT 'шаблон звукового приглашения. null или 0... - использовать родительский.',
  `expectation` int(11) NOT NULL DEFAULT '0' COMMENT 'Время обязательного ожидания посетителя',
  `link_service_id` bigint(20) DEFAULT NULL COMMENT 'Услуга, в которую реально попадет клиент. А эта сама услуга чисто кнопка, ярлык.',
  PRIMARY KEY (`id`),
  KEY `idx_servises_parent_id_servises_id` (`prent_id`),
  KEY `idx_services_shedule` (`schedule_id`),
  KEY `idx_services_calendar` (`calendar_id`),
  KEY `fk_services_services1_idx` (`link_service_id`),
  CONSTRAINT `fk_services_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_services_link_services` FOREIGN KEY (`link_service_id`) REFERENCES `services` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_services_shedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_servises_parent_id_servises_id` FOREIGN KEY (`prent_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Дерево услуг';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (1,'Дерево услуг','Дерево услуг','-','<html><p align=center><span style=\'font-size:55.0;color:#DC143C\'>Система управления очередью</span><br><span style=\'font-size:45.0;color:#DC143C\'><i>выберите требуемую услугу</i>',1,1,NULL,0,0,1,14,60,NULL,0,'',0,0,0,NULL,'','',0,NULL,'',0,100,100,200,100,NULL,1,'120050',0,NULL),(2,'Услуга','Описание услуги','А','<html><b><p align=center><span style=\'font-size:20.0pt;color:blue\'>Некая услуга',1,1,1,0,0,1,14,60,1,0,'',0,0,0,1,'','',0,NULL,'',0,100,100,200,100,NULL,1,'021111',0,NULL);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_langs`
--

DROP TABLE IF EXISTS `services_langs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_langs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `services_id` bigint(20) DEFAULT NULL,
  `lang` varchar(45) NOT NULL,
  `name` varchar(2000) NOT NULL DEFAULT '',
  `description` varchar(2000) DEFAULT NULL,
  `button_text` varchar(2500) NOT NULL DEFAULT '',
  `input_caption` varchar(2000) NOT NULL DEFAULT '',
  `ticket_text` varchar(1500) DEFAULT NULL,
  `tablo_text` varchar(1500) NOT NULL DEFAULT '',
  `pre_info_html` text NOT NULL,
  `pre_info_print_text` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_services_langs_services` (`services_id`),
  CONSTRAINT `fk_services_langs_services` FOREIGN KEY (`services_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_langs`
--

LOCK TABLES `services_langs` WRITE;
/*!40000 ALTER TABLE `services_langs` DISABLE KEYS */;
/*!40000 ALTER TABLE `services_langs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_users`
--

DROP TABLE IF EXISTS `services_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `coefficient` int(11) NOT NULL DEFAULT '1' COMMENT 'Коэффициент участия. 0/1/2',
  `flexible_coef` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'возможность изменить коэф участия юзерами',
  PRIMARY KEY (`id`),
  KEY `idx_services_id_su_service_id` (`service_id`),
  KEY `idx_userss_id_su_user_id` (`user_id`),
  CONSTRAINT `fk_services_id_su_service_id` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_userss_id_su_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='Таблица соответствий услуга - пользователь.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_users`
--

LOCK TABLES `services_users` WRITE;
/*!40000 ALTER TABLE `services_users` DISABLE KEYS */;
INSERT INTO `services_users` VALUES (1,2,2,1,0);
/*!40000 ALTER TABLE `services_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spec_schedule`
--

DROP TABLE IF EXISTS `spec_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spec_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_from` date NOT NULL COMMENT 'Это спец расписание для этого календаря действует с этой даты',
  `date_to` date NOT NULL COMMENT 'Это спец расписание для этого календаря действует до этой даты',
  `calendar_id` bigint(20) NOT NULL,
  `schedule_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_spec_schedule_calendar` (`calendar_id`),
  KEY `idx_spec_schedule_schedule` (`schedule_id`),
  CONSTRAINT `fk_spec_schedule_calendar` FOREIGN KEY (`calendar_id`) REFERENCES `calendar` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_spec_schedule_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Специальные расписания для периодов для конкретных календарей. Перекрывают стардартное расписание.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spec_schedule`
--

LOCK TABLES `spec_schedule` WRITE;
/*!40000 ALTER TABLE `spec_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `spec_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `standards`
--

DROP TABLE IF EXISTS `standards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `standards` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wait_max` int(11) NOT NULL DEFAULT '0' COMMENT 'Максимальное время ожидания, в минутах',
  `work_max` int(11) NOT NULL DEFAULT '0' COMMENT 'Максимальное время работы с одним клиентом, в минутах',
  `downtime_max` int(11) NOT NULL DEFAULT '0' COMMENT 'Максимальное время простоя при наличии очереди, в минутах',
  `line_service_max` int(11) NOT NULL DEFAULT '0' COMMENT 'Максимальная длинна очереди к одной услуге',
  `line_total_max` int(11) NOT NULL DEFAULT '0' COMMENT 'Максимальное количество клиентов ко всем услугам',
  `relocation` int(11) NOT NULL DEFAULT '1' COMMENT 'типа параметр если есть перемещение, например между корпусами или ходьба до оператора',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `standards`
--

LOCK TABLES `standards` WRITE;
/*!40000 ALTER TABLE `standards` DISABLE KEYS */;
INSERT INTO `standards` VALUES (1,10,20,10,10,20,1);
/*!40000 ALTER TABLE `standards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistic`
--

DROP TABLE IF EXISTS `statistic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `service_id` bigint(20) NOT NULL,
  `results_id` bigint(20) DEFAULT NULL,
  `user_start_time` datetime NOT NULL COMMENT 'Время начала обработки кастомера юзером.',
  `user_finish_time` datetime NOT NULL COMMENT 'Время завершения обработки кастомера юзером.',
  `client_stand_time` datetime NOT NULL COMMENT 'Время постановки кастомера в очередь',
  `user_work_period` int(11) NOT NULL COMMENT 'Время работы пользователя с клиентом в минутах.',
  `client_wait_period` int(11) NOT NULL COMMENT 'Время ожидания в минутах. Определяется триггером.',
  `state_in` int(11) NOT NULL DEFAULT '0' COMMENT 'Клиент перешел в это состояние',
  PRIMARY KEY (`id`),
  KEY `idx_work_user_id_users_id` (`user_id`),
  KEY `idx_work_сlient_id_сlients_id` (`client_id`),
  KEY `idx_work_service_id_services_id` (`service_id`),
  KEY `idx_statistic_results` (`results_id`),
  CONSTRAINT `fk_statistic_results` FOREIGN KEY (`results_id`) REFERENCES `results` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_work_service_id_services_id` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_user_id_users_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_work_сlient_id_сlients_id` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='События работы пользователя с клиентом.Формируется триггером';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistic`
--

LOCK TABLES `statistic` WRITE;
/*!40000 ALTER TABLE `statistic` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `streets`
--

DROP TABLE IF EXISTS `streets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `streets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT 'Наименование улицы.',
  PRIMARY KEY (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Словарь улиц';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `streets`
--

LOCK TABLES `streets` WRITE;
/*!40000 ALTER TABLE `streets` DISABLE KEYS */;
/*!40000 ALTER TABLE `streets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_operation_types`
--

DROP TABLE IF EXISTS `user_operation_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_operation_types` (
  `id` int(11) NOT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_operation_types`
--

LOCK TABLES `user_operation_types` WRITE;
/*!40000 ALTER TABLE `user_operation_types` DISABLE KEYS */;
INSERT INTO `user_operation_types` VALUES (1,'время работы'),(2,'обслуживание клиента'),(3,'техническое время'),(4,'простаивание 1'),(5,'перерыв'),(6,'простаивание 2');
/*!40000 ALTER TABLE `user_operation_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор пользователя.',
  `name` varchar(150) NOT NULL COMMENT 'Наименование',
  `password` varchar(45) NOT NULL COMMENT 'Пароль пользователя.',
  `point` varchar(45) NOT NULL COMMENT 'Идентификация рабочего места',
  `adress_rs` smallint(6) NOT NULL DEFAULT '0' COMMENT 'Адрес табло  пользователя в герлянде RS485',
  `enable` int(11) NOT NULL DEFAULT '1' COMMENT 'Дейсткующий пользователь или удаленный.',
  `admin_access` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Доступ к администрирования системы.',
  `report_access` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Доступ к получению отчетов.',
  `parallel_access` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Разрешение вести параллельный прием',
  `point_ext` varchar(1045) NOT NULL DEFAULT '' COMMENT 'Вывод в третью колонку на главном табло. html + клиент ###  окно @@@',
  `tablo_text` varchar(1500) NOT NULL DEFAULT '' COMMENT 'Текст для вывода на главное табло в шаблоны панели вызванного и третью колонку пользователя',
  `deleted` date DEFAULT NULL COMMENT 'признак удаления с проставлением даты',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='Пользователи системы.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Администратор','','1',32,1,1,1,0,'','',NULL),(2,'Пользователь','jqcruDLoYAfNvvJBbIuYtQ==','2',33,1,0,0,0,'<html><span style=\'font-size:26.0pt;color:blue\'>Этаж 1<br>Кабинет 1 #user #service #inputed','',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_statistic`
--

DROP TABLE IF EXISTS `users_statistic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users_statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `operation_id` int(11) DEFAULT NULL,
  `dt` datetime NOT NULL,
  `dt_start` datetime NOT NULL,
  `dt_stop` datetime NOT NULL,
  `service_id` bigint(20) DEFAULT NULL,
  `state_in` int(11) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `place_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=653 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_statistic`
--

LOCK TABLES `users_statistic` WRITE;
/*!40000 ALTER TABLE `users_statistic` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_statistic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `version_software`
--

DROP TABLE IF EXISTS `version_software`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `version_software` (
  `id` int(11) NOT NULL,
  `version` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Версия ПО';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `version_software`
--

LOCK TABLES `version_software` WRITE;
/*!40000 ALTER TABLE `version_software` DISABLE KEYS */;
INSERT INTO `version_software` VALUES (1,'17.1.2');
/*!40000 ALTER TABLE `version_software` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Table structure for table `version_software`
--

DROP TABLE IF EXISTS `notifications_info`;
CREATE TABLE `qsystem`.`notifications_info` (
  `id` INT NOT NULL,
  `fio` VARCHAR(65) NOT NULL,
  `email` VARCHAR(80) NOT NULL,
  `phone` VARCHAR(8) NULL,
  PRIMARY KEY (`id`))
COMMENT = 'Информация для уведомлений об очередях';

--
-- Table structure for table `email_sending_settings`
--

DROP TABLE IF EXISTS `email_sending_settings`;
CREATE TABLE `qsystem`.`email_sending_settings` (
  `id` INT NOT NULL,
  `smtp_host` VARCHAR(45) NOT NULL COMMENT 'Адрес SMTP сервера',
  `smtp_port` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL COMMENT 'Email адрес отправителя',
  `password` VARCHAR(45) NOT NULL COMMENT 'Пароль для email',
  `subject` VARCHAR(45) NOT NULL COMMENT 'Заголовок сообщения',
  `message` VARCHAR(100) NOT NULL COMMENT 'Текст сообщения',
  PRIMARY KEY (`id`))
COMMENT = 'Настроечные данные для отправки почты. Адрес SMTP сервера, пароль и логин почтового ящика, с которого отправляются письма и т.д.';
