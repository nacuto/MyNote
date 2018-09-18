package com.burytomorrow.final_project;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by BuryTomorrow on 2018/1/7.
 */

public class MyDB extends Application{
    private DB_Helper db_helper ;
    public void init(){
        db_helper = new DB_Helper(this) ;
    }
    public DB_Helper getDB(){
        return db_helper;
    }
}
