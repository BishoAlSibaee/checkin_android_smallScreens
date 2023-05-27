package com.syriasoft.hotelservices.Interface;

public interface RequestCallback {

    void onSuccess();
    void onFail(String error);
}
