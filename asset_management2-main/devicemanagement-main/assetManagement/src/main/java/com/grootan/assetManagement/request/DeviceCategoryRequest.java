package com.grootan.assetManagement.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeviceCategoryRequest {

    private String category;

    public DeviceCategoryRequest(String category) {
        this.category = category;
    }

    public DeviceCategoryRequest(long id, String device) {
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
