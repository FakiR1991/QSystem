/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.client.model;

import java.awt.event.MouseEvent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**
 *
 * @author zaikov
 */
public class MySelectionTreeUI extends BasicTreeUI {

    @Override
    protected void selectPathForEvent(TreePath path, MouseEvent event) {
        super.selectPathForEvent(path, event); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected boolean isToggleSelectionEvent(MouseEvent event) {
        return super.isToggleSelectionEvent(event); //To change body of generated methods, choose Tools | Templates.
    }
}
