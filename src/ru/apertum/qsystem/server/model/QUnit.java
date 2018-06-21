/*
 * Copyright (C) 2018 Apertum Project LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author zaikov
 */
@Entity
@Table(name = "units")
public class QUnit implements IidGetter, Serializable {
    @Expose
    @SerializedName("id")
    private Long id = null;

    @Id
    @Column(name = "id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Название отделения
     */
    @Expose
    @SerializedName("name")
    private String name;

    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "name")
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}