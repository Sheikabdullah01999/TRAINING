package com.grootan.assetManagement.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class sample {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int jk;
    private String jkl;
    private String as;

    public int getJk() {
        return jk;
    }

    public void setJk(int jk) {
        this.jk = jk;
    }

    public String getJkl() {
        return jkl;
    }

    public void setJkl(String jkl) {
        this.jkl = jkl;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }
}
