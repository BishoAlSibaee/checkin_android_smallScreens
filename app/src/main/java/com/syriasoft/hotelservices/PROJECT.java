package com.syriasoft.hotelservices;

import java.util.ArrayList;
import java.util.List;

public class PROJECT {
    int id ;
    public String projectName ;
    String city ;
    String salesman ;
    String TuyaUser;
    String TuyaPassword;
    String LockUser;
    String LockPassword;
    String url;

    public PROJECT(int id, String projectName, String city, String salesman, String tuyaUser, String tuyaPassword, String lockUser, String lockPassword, String url) {
        this.id = id;
        this.projectName = projectName;
        this.city = city;
        this.salesman = salesman;
        TuyaUser = tuyaUser;
        TuyaPassword = tuyaPassword;
        LockUser = lockUser;
        LockPassword = lockPassword;
        this.url = url;
    }

    public List<PROJECT> makeProjectsList (List<Object> list) {
        List<PROJECT> projects = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            PROJECT x = (PROJECT) list.get(i);
            projects.add(x);
        }
        return projects ;
    }
}

