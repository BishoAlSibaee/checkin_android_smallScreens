package com.syriasoft.cleanup;

public class User {
    public int id;
    public String name;
    public int jobNumber;
    public int Mobile;
    public String department;
    public String token;
    public String control;
    public int logedin ;

    public User(int id, String name, int jobNumber, int mobile, String department, String token,String control,int logedin) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        Mobile = mobile;
        this.department = department;
        this.token = token;
        this.control = control;
        this.logedin = logedin;
    }
}
