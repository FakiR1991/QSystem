/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractListModel;
import ru.apertum.qsystem.server.QSession;

/**
 *
 * @author zaikov
 */
public class QSessionList extends AbstractListModel implements List {

    private final LinkedList<QSession> sessions;
    
    public QSessionList() {
        this.sessions = new LinkedList<>();
    }
    
    public QSessionList(LinkedList<QSession> sessions) {
        this.sessions = new LinkedList<>(sessions);
    }

    @Override
    public int getSize() {
        return this.sessions.size();
    }

    @Override
    public QSession getElementAt(int index) {
        return sessions.get(index);
    }
    
    public boolean removeElement(QSession obj) {
        final int index = this.sessions.indexOf(obj);
        final boolean res = this.sessions.remove(obj);
        fireIntervalRemoved(this, index, index);
        return res;
    }

    public void addElement(QSession obj) {
        final int index = this.sessions.size();
        this.sessions.add(obj);
        fireIntervalAdded(this, index, index);
    }
    
    @Override
    public int size() {
        return getSize();
    }
    
    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.sessions.contains(o);
    }

    @Override
    public Iterator iterator() {
        return this.sessions.iterator();
    }

    @Override
    public QSession[] toArray() {
        return (QSession[])this.sessions.toArray();
    }

    @Override
    public boolean add(Object e) {
        return this.sessions.add((QSession) e);
    }

    @Override
    public boolean remove(Object o) {
        return this.sessions.remove((QSession)o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return this.sessions.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        final int index = this.sessions.size();
        boolean res = this.sessions.addAll(c);
        fireIntervalAdded(this, index, index);
        return res;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return this.sessions.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection c) {
        final int index = this.sessions.size();
        boolean res = this.sessions.removeAll(c);
        fireIntervalAdded(this, index, index);
        return res;
    }

    @Override
    public boolean retainAll(Collection c) {
        return this.sessions.retainAll(c);
    }

    @Override
    public void clear() {
        this.sessions.clear();
    }

    @Override
    public QSession get(int index) {
        return this.sessions.get(index);
    }

    @Override
    public QSession set(int index, Object element) {
        return this.sessions.set(index, (QSession)element);
    }

    @Override
    public void add(int index, Object element) {
        this.sessions.add(index, (QSession)element);
    }

    @Override
    public QSession remove(int index) {
        return this.sessions.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.sessions.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.sessions.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return this.sessions.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return this.sessions.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return this.sessions.subList(fromIndex, toIndex);
    }

    @Override
    public QSession[] toArray(Object[] a) {
        return (QSession[])this.sessions.toArray(a);
    }
    
    public static QSessionList getInstance() {
        return QSessionList.QSessionListHolder.INSTANCE;
    }
    
    private static class QSessionListHolder {
        private static final QSessionList INSTANCE = new QSessionList();
    }
}