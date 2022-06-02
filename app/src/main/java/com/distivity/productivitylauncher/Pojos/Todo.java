package com.distivity.productivitylauncher.Pojos;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName="todos")
public class Todo {


    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    private boolean checked;
    private int parrentId;
    private boolean isForToday;


    public Todo(int id, String name, boolean checked, int parrentId, boolean isForToday) {
        this.id = id;
        this.name = name;
        this.checked = checked;
        this.parrentId = parrentId;
        this.isForToday = isForToday;
    }

    public boolean isChecked() {
        return checked;
    }

    public Todo setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }

    public int getParrentId() {
        return parrentId;
    }

    public void setParrentId(int parrentId) {
        this.parrentId = parrentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Todo setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isForToday() {
        return isForToday;
    }

    public Todo setForToday(boolean forToday) {
        isForToday = forToday;
        return this;
    }

}
