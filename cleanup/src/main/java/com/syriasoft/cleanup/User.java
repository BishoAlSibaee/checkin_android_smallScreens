package com.syriasoft.cleanup;

public class User
{
    int id;
    String name;
    int jobNumber ;
    int Mobile ;
    String department;
    String token ;

    public User(int id, String name, int jobNumber, int mobile, String department, String token) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        Mobile = mobile;
        this.department = department;
        this.token = token;
    }
}
