package com.grootan.assetManagement;

public class Response<T> {
    private String code;
    private String status;
    private String description;
    private T data;

    public Response()
    {

    }

    public Response(String code, String status, String description) {
        this.code = code;
        this.status = status;
        this.description = description;
    }

    public Response(String code, String status, String description, T data) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
