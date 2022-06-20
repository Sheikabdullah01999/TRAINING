package com.grootan.assetManagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}