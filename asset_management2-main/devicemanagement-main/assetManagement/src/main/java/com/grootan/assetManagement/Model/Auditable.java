package com.grootan.assetManagement.Model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import java.util.Date;
import static javax.persistence.TemporalType.*;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U>
{
    @CreatedBy
    protected U createdBy;
    @CreatedDate
    @Temporal(TIMESTAMP)
    protected Date creationDate;
    @LastModifiedBy
    protected U lastModifiedBy;
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    protected Date lastModifiedDate;


    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(U createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public U getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
