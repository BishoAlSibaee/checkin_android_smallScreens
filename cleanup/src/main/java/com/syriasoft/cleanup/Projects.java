package com.syriasoft.cleanup;

public class Projects {
    public int id;
    public String projectName;
    public String city;
    public String salesman;
    public String TuyaUser;
    public String TuyaPassword;
    public String LockUser;
    public String LockPassword;
    public String Url ;

    public Projects(int id, String projectName, String city, String salesman, String tuyaUser, String tuyaPassword, String lockUser, String lockPassword,String url) {
        this.id = id;
        this.projectName = projectName;
        this.city = city;
        this.salesman = salesman;
        TuyaUser = tuyaUser;
        TuyaPassword = tuyaPassword;
        LockUser = lockUser;
        LockPassword = lockPassword;
        Url = url ;
    }
}
