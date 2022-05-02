package com.grootan.assetManagement.Model;


import javax.persistence.*;

@Entity
public class History extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;


    @Column(name="history")
    private String history;

    public History() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public History(String history) {
        this.history = history;
    }
}
