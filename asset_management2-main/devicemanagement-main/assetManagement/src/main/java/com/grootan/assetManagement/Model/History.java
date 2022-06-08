package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;
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