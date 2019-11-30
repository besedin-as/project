package com.example.project.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FilePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int scrollTop;
    private int scrollLeft;

    FilePosition() {}

    public FilePosition(int scrollTop, int scrollLeft) {
        this.scrollTop = scrollTop;
        this.scrollLeft = scrollLeft;
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(int scrollLeft) {
        this.scrollLeft = scrollLeft;
    }

    public int getScrollTop() {
        return scrollTop;
    }

    public void setScrollTop(int scrollTop) {
        this.scrollTop = scrollTop;
    }
}
