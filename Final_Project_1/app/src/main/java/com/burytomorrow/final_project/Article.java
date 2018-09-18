package com.burytomorrow.final_project;

/**
 * Created by BuryTomorrow on 2018/1/3.
 */

public class Article {
    Article(int articleID, String article , int year,int month,int day ,String time,int lock,String password){
        this.articleID=articleID;
        this.article=article;
        this.year=year;
        this.month=month;
        this.day = day;
        this.time=time;
        this.lock=false;
        this.check=false;
        if(lock>0) this.lock = true;
        else this.lock = false;
        if(password!=null) this.password = password;
    }
    public int articleID;
    public String title;
    public String article;//正文
    public int year; //年
    public int month;//月
    public int day;  //日
    public String time;   //时间
    public boolean lock;  //是否加密
    String password; //密码
    public boolean check; //是否被选择了

}
