package com.grootan.assetManagement.Model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String creationDate;

    private String createdBy;

    private String action;

    @Column(name="history")
    private String history;

    public History(String createdBy,String action, String history,String date) {
        this.createdBy = createdBy;
        this.action=action;
        this.history = history;
        this.creationDate=date;
    }
    public History()
    {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public History(String history) {
        this.history = history;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
