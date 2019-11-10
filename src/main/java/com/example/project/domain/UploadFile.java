package com.example.project.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UploadFile {

    public UploadFile(String filePath) {
        this.filePath = filePath;
    }

    public UploadFile() {}

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
