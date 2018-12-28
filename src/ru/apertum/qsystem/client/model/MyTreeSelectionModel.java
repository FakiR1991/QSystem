/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.client.model;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author zaikov
 */
public class MyTreeSelectionModel extends DefaultTreeSelectionModel {

//    @Override
//    public void addTreeSelectionListener(TreeSelectionListener x) {
//        super.addTreeSelectionListener(x); //To change body of generated methods, choose Tools | Templates.
////        посмотреть как это будет работать
////        
////        два метода переопределённых ниже работают только с зажатым контролом
//    }
//    
//    @Override
//    public void addSelectionPath(TreePath path) {
//        if (path != null) {
//            if (isPathSelected(path)) {
//                // If path has been previously selected REMOVE THE SELECTION.
//                super.removeSelectionPath(path);
//            } else {
//                // Else we really want to add the selection...
//                super.addSelectionPath(path);
//            }
//        }
//    }
//    
//    @Override
//    public void addSelectionPaths(TreePath[] paths) {
//        if (paths != null) {
//            for (TreePath path : paths) {
//
//                TreePath[] toAdd = new TreePath[1];
//                toAdd[0] = path;
//
//                if (isPathSelected(path)) {
//                    // If path has been previously selected REMOVE THE SELECTION.
//                    super.removeSelectionPaths(toAdd);
//                } else {
//                    // Else we really want to add the selection...
//                    super.addSelectionPaths(toAdd);
//                }
//            }
//        }
//    }
}
