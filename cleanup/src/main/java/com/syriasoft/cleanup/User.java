package com.syriasoft.cleanup;

public class User
{
    public int id;
    public String name;
    public int jobNumber ;
    public int Mobile ;
    public String department;
    public String token ;

    public User(int id, String name, int jobNumber, int mobile, String department, String token) {
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        Mobile = mobile;
        this.department = department;
        this.token = token;
    }
}
