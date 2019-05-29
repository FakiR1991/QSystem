/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model.schedule;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.IidGetter;

/**
 *
 * @author zaikov
 */
@Entity
@Table(name = "schedule_2")
public class QSchedule2 implements IidGetter, Serializable {

    public QSchedule2() { }

    @Id
    @Column(name = "id")
    private Long id = new Date().getTime();

    @Override
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof QSchedule2)) {
            throw new TypeNotPresentException("Неправильный тип для сравнения", new ServerException("Неправильный тип для сравнения"));
        }
        return id.equals(((QSchedule2)o).id);
    }

    @Override
    public int hashCode() {
        return (int)(this.id != null ? this.id : 0);
    }
    
    /**
     * Наименование плана.
     */
    @Column(name = "name")
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    
//    /**
//     * Тип услуг для которых это расписание (СПиАО или СЦ);
//     * 1 - СПиАО, 2 - СЦ
//     */
//    @Column(name = "type")
//    private Integer type;
//    
//    public Integer getType() {
//        return type;
//    }
//
//    public void setType(Integer type) {
//        this.type = type;
//    }
//    
//    /**
//     * ИД отделения
//     */
//    @Column(name = "unit_id")
//    private Integer unitId;
//    
//    public Integer getUnitId() {
//        return unitId;
//    }
//
//    public void setUnitId(Integer unitId) {
//        this.unitId = unitId;
//    }
    
    /**
     * ИД отделения
     */
//    private QUnit unitId;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "unit_id")
//    public QUnit getUnitId() {
//        return unitId;
//    }
//
//    public void setUnitId(QUnit unitId) {
//        this.unitId = unitId;
//    }
    
    /**
     * Понедельник
     */
    @Column(name = "day1_begin")
    @Temporal(TemporalType.TIME)
    private Date day1Begin;

    public Date getDay1Begin() {
        return day1Begin;
    }

    public void setDay1Begin(Date day1Begin) {
        this.day1Begin = day1Begin;
    }
    
    @Column(name = "day1_end")
    @Temporal(TemporalType.TIME)
    private Date day1End;

    public Date getDay1End() {
        return day1End;
    }

    public void setDay1End(Date day1End) {
        this.day1End = day1End;
    }
    
    /**
     * Вторник
     */
    @Column(name = "day2_begin")
    @Temporal(TemporalType.TIME)
    private Date day2Begin;

    public Date getDay2Begin() {
        return day2Begin;
    }

    public void setDay2Begin(Date day2Begin) {
        this.day2Begin = day2Begin;
    }
    
    @Column(name = "day2_end")
    @Temporal(TemporalType.TIME)
    private Date day2End;

    public Date getDay2End() {
        return day2End;
    }

    public void setDay2End(Date day2End) {
        this.day2End = day2End;
    }
    
    /**
     * Среда
     */
    @Column(name = "day3_begin")
    @Temporal(TemporalType.TIME)
    private Date day3Begin;

    public Date getDay3Begin() {
        return day3Begin;
    }

    public void setDay3Begin(Date day3Begin) {
        this.day3Begin = day3Begin;
    }
    
    @Column(name = "day3_end")
    @Temporal(TemporalType.TIME)
    private Date day3End;

    public Date getDay3End() {
        return day3End;
    }

    public void setDay3End(Date day3End) {
        this.day3End = day3End;
    }
    
    /**
     * Четверг
     */
    @Column(name = "day4_begin")
    @Temporal(TemporalType.TIME)
    private Date day4Begin;

    public Date getDay4Begin() {
        return day4Begin;
    }

    public void setDay4Begin(Date day4Begin) {
        this.day4Begin = day4Begin;
    }
    
    @Column(name = "day4_end")
    @Temporal(TemporalType.TIME)
    private Date day4End;

    public Date getDay4End() {
        return day4End;
    }

    public void setDay4End(Date day4End) {
        this.day4End = day4End;
    }
    
    /**
     * Пятница
     */
    @Column(name = "day5_begin")
    @Temporal(TemporalType.TIME)
    private Date day5Begin;

    public Date getDay5Begin() {
        return day5Begin;
    }

    public void setDay5Begin(Date day5Begin) {
        this.day5Begin = day5Begin;
    }
    
    @Column(name = "day5_end")
    @Temporal(TemporalType.TIME)
    private Date day5End;

    public Date getDay5End() {
        return day5End;
    }

    public void setDay5End(Date day5End) {
        this.day5End = day5End;
    }
    
    /**
     * Суббота
     */
    @Column(name = "day6_begin")
    @Temporal(TemporalType.TIME)
    private Date day6Begin;

    public Date getDay6Begin() {
        return day6Begin;
    }

    public void setDay6Begin(Date day6Begin) {
        this.day6Begin = day6Begin;
    }
    
    @Column(name = "day6_end")
    @Temporal(TemporalType.TIME)
    private Date day6End;

    public Date getDay6End() {
        return day6End;
    }

    public void setDay6End(Date day6End) {
        this.day6End = day6End;
    }
    
    /**
     * Воскресенье
     */
    @Column(name = "day7_begin")
    @Temporal(TemporalType.TIME)
    private Date day7Begin;

    public Date getDay7Begin() {
        return day7Begin;
    }

    public void setDay7Begin(Date day7Begin) {
        this.day7Begin = day7Begin;
    }
    
    @Column(name = "day7_end")
    @Temporal(TemporalType.TIME)
    private Date day7End;

    public Date getDay7End() {
        return day7End;
    }

    public void setDay7End(Date day7End) {
        this.day7End = day7End;
    }
    
    /**
     * Перерыв - понедельник
     */
    @Column(name = "break1_begin")
    @Temporal(TemporalType.TIME)
    private Date break1Begin;

    public Date getBreak1Begin() {
        return break1Begin;
    }

    public void setBreak1Begin(Date break1Begin) {
        this.break1Begin = break1Begin;
    }
    
    @Column(name = "break1_end")
    @Temporal(TemporalType.TIME)
    private Date break1End;

    public Date getBreak1End() {
        return break1End;
    }

    public void setBreak1End(Date break1End) {
        this.break1End = break1End;
    }
    
    /**
     * Перерыв - вторник
     */
    @Column(name = "break2_begin")
    @Temporal(TemporalType.TIME)
    private Date break2Begin;

    public Date getBreak2Begin() {
        return break2Begin;
    }

    public void setBreak2Begin(Date break2Begin) {
        this.break2Begin = break2Begin;
    }
    
    @Column(name = "break2_end")
    @Temporal(TemporalType.TIME)
    private Date break2End;

    public Date getBreak2End() {
        return break2End;
    }

    public void setBreak2End(Date break2End) {
        this.break2End = break2End;
    }
    
    /**
     * Перерыв - среда
     */
    @Column(name = "break3_begin")
    @Temporal(TemporalType.TIME)
    private Date break3Begin;

    public Date getBreak3Begin() {
        return break3Begin;
    }

    public void setBreak3Begin(Date break3Begin) {
        this.break3Begin = break3Begin;
    }
    
    @Column(name = "break3_end")
    @Temporal(TemporalType.TIME)
    private Date break3End;

    public Date getBreak3End() {
        return break3End;
    }

    public void setBreak3End(Date break3End) {
        this.break3End = break3End;
    }
    
    /**
     * Перерыв - четверг
     */
    @Column(name = "break4_begin")
    @Temporal(TemporalType.TIME)
    private Date break4Begin;

    public Date getBreak4Begin() {
        return break4Begin;
    }

    public void setBreak4Begin(Date break4Begin) {
        this.break4Begin = break4Begin;
    }
    
    @Column(name = "break4_end")
    @Temporal(TemporalType.TIME)
    private Date break4End;

    public Date getBreak4End() {
        return break4End;
    }

    public void setBreak4End(Date break4End) {
        this.break4End = break4End;
    }
    
    /**
     * Перерыв - пятница
     */
    @Column(name = "break5_begin")
    @Temporal(TemporalType.TIME)
    private Date break5Begin;

    public Date getBreak5Begin() {
        return break5Begin;
    }

    public void setBreak5Begin(Date break5Begin) {
        this.break5Begin = break5Begin;
    }
    
    @Column(name = "break5_end")
    @Temporal(TemporalType.TIME)
    private Date break5End;

    public Date getBreak5End() {
        return break5End;
    }

    public void setBreak5End(Date break5End) {
        this.break5End = break5End;
    }
    
    /**
     * Перерыв - суббота
     */
    @Column(name = "break6_begin")
    @Temporal(TemporalType.TIME)
    private Date break6Begin;

    public Date getBreak6Begin() {
        return break6Begin;
    }

    public void setBreak6Begin(Date break6Begin) {
        this.break6Begin = break6Begin;
    }
    
    @Column(name = "break6_end")
    @Temporal(TemporalType.TIME)
    private Date break6End;

    public Date getBreak6End() {
        return break6End;
    }

    public void setBreak6End(Date break6End) {
        this.break6End = break6End;
    }
    
    /**
     * Перерыв - воскресенье
     */
    @Column(name = "break7_begin")
    @Temporal(TemporalType.TIME)
    private Date break7Begin;

    public Date getBreak7Begin() {
        return break7Begin;
    }

    public void setBreak7Begin(Date break7Begin) {
        this.break7Begin = break7Begin;
    }
    
    @Column(name = "break7_end")
    @Temporal(TemporalType.TIME)
    private Date break7End;

    public Date getBreak7End() {
        return break7End;
    }

    public void setBreak7End(Date break7End) {
        this.break7End = break7End;
    }
    
    /**
     * Начало и конец рабочего дня.
     */
    public static class Interval {
        public final Date start;
        public final Date finish;
        public final Date breakStart;
        public final Date breakEnd;

        public Interval(Date start, Date finish, Date breakStart, Date breakEnd) {
            if (start == null || finish == null) {
                this.start = new Date(111);
                this.finish = new Date(222);
                this.breakStart = new Date(333);
                this.breakEnd = new Date(444);
            } else {
                if (finish.before(start)) {
                    throw new ServerException("Finish date " + finish + " before than start date " + start);
                }
                this.start = start;
                this.finish = finish;
                this.breakStart = breakStart;
                this.breakEnd = breakEnd;
            }
        }
        
        public Interval(Date start, Date finish) {
            if (start == null || finish == null) {
                this.start = new Date(111);
                this.finish = new Date(222);
                this.breakStart = new Date(333);
                this.breakEnd = new Date(444);
            } else {
                if (finish.before(start)) {
                    throw new ServerException("Finish date " + finish + " before than start date " + start);
                }
                this.start = start;
                this.finish = finish;
                this.breakStart = null;
                this.breakEnd = null;
            }
        }

        public long diff() {
            return finish.getTime() - start.getTime();
        }
    }

    public Interval getWorkInterval(Date date) {
        // Определим время начала и конца работы на этот день
        final GregorianCalendar gc_day = new GregorianCalendar();
        gc_day.setTime(date);
        final Interval in;
        final Date breakBegin;
        final Date breakEnd;
        switch (gc_day.get(GregorianCalendar.DAY_OF_WEEK)) {
            case 2:
                in = new Interval(getDay1Begin(), getDay1End());
                breakBegin = getBreak1Begin();
                breakEnd = getBreak1End();
                break;
            case 3:
                in = new Interval(getDay2Begin(), getDay2End());
                breakBegin = getBreak2Begin();
                breakEnd = getBreak2End();
                break;
            case 4:
                in = new Interval(getDay3Begin(), getDay3End());
                breakBegin = getBreak3Begin();
                breakEnd = getBreak3End();
                break;
            case 5:
                in = new Interval(getDay4Begin(), getDay4End());
                breakBegin = getBreak4Begin();
                breakEnd = getBreak4End();
                break;
            case 6:
                in = new Interval(getDay5Begin(), getDay5End());
                breakBegin = getBreak5Begin();
                breakEnd = getBreak5End();
                break;
            case 7:
                in = new Interval(getDay6Begin(), getDay6End());
                breakBegin = getBreak6Begin();
                breakEnd = getBreak6End();
                break;
            case 1:
                in = new Interval(getDay7Begin(), getDay7End());
                breakBegin = getBreak7Begin();
                breakEnd = getBreak7End();
                break;
            default:
                throw new ServerException("Wrong day of week!");
        }
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(in.start);
        gc_day.set(GregorianCalendar.HOUR_OF_DAY, gc.get(GregorianCalendar.HOUR_OF_DAY));
        gc_day.set(GregorianCalendar.MINUTE, gc.get(GregorianCalendar.MINUTE));
        gc_day.set(GregorianCalendar.SECOND, 0);
        final Date ds = gc_day.getTime();
        gc.setTime(in.finish);
        gc_day.setTime(date);
        gc_day.set(GregorianCalendar.HOUR_OF_DAY, gc.get(GregorianCalendar.HOUR_OF_DAY));
        gc_day.set(GregorianCalendar.MINUTE, gc.get(GregorianCalendar.MINUTE));
        gc_day.set(GregorianCalendar.SECOND, 0);
        return new Interval(ds, gc_day.getTime(), breakBegin, breakEnd);
    }
}