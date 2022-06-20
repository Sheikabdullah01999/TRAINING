package com.grootan.assetManagement.response;

import org.springframework.http.HttpStatus;

public class Response<T>
{
    private String subsystem;
    private String code;
    private HttpStatus status;
    private String description;
    private T data;

    public Response()
    {

    }

    public Response(String code, HttpStatus status, String description) {
        this.code = code;
        this.status = status;
        this.description = description;
    }

    public Response(String code, HttpStatus status, String description, T data) {
        this.code = code;
        this.status = status;
        this.description = description;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
