package com.burytomorrow.final_project;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

/**
 * Created by BuryTomorrow on 2018/1/20.
 */

public class DBActivity extends AppCompatActivity {
    private Connection connection = null;
    private boolean flag = false;
    private boolean done = false;
    private String msg;
    private ResultSet rs = null;
    private String sql;
    private int cnt;
    private String type;
    private DB_Helper db_helper =new DB_Helper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting);

        type = getIntent().getExtras().getString("type") ;
        new Thread(query).start();
        finish();
    }
    Handler myHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = new Bundle();
        }
    };
    Runnable query = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = (Connection) DriverManager.getConnection("jdbc:mysql://120.79.152.128:3306/android db?autoReconnect=true&failOverReadOnly=false", "user", "root");
                Statement stmt= (Statement) connection.createStatement();
                flag = true;
                if(type.equals("from")){
                    //Toast.makeText(DBActivity.this,"进来了",Toast.LENGTH_SHORT).show();
                    //先删除本地数据库内容
                    SQLiteDatabase db  = db_helper.getReadableDatabase() ;
                    db_helper.getWritableDatabase().execSQL("DELETE FROM Article WHERE 1 = 1");
                    //从云端拉取数据加入到本地数据库当中
                    sql = "select * from article";
                    rs=stmt.executeQuery(sql);
                    while (rs.next()){
                        Calendar calendar=Calendar.getInstance();
                        ContentValues values = new ContentValues() ;
                        values.put("article",rs.getString("article"));
                        values.put("year",rs.getInt("year"));
                        values.put("month",rs.getInt("month"));
                        values.put("day",rs.getInt("day"));
                        values.put("time",rs.getString("time"));
                        values.put("locked",rs.getInt("locked"));
                        values.put("password",rs.getString("password"));
                        db_helper.getWritableDatabase().insert("Article",null,values) ;
                    }
                    EventBus.getDefault().post("1");
                }
                //如果是同步到云端，则先将云数据删除，然后再进行同步
                else if(type.equals("to")){
                    //Toast.makeText(DBActivity.this,"进来了",Toast.LENGTH_SHORT).show();
                    //删除云端数据
                    Statement stmt2= (Statement) connection.createStatement();
                    sql = "DELETE FROM article WHERE 1 = 1";
                    cnt=stmt.executeUpdate(sql);
                    //将本地数据库添加到云端
                    SQLiteDatabase db  = db_helper.getReadableDatabase() ;
                    Cursor cursor = db.rawQuery("SELECT * FROM Article", null);
                    if (cursor.moveToFirst()) {
                        do {
                            sql = "insert into article(article,year,month,day,time,locked,password) values('"+
                                    cursor.getString(cursor.getColumnIndex("article"))+"','"+
                                    cursor.getInt(cursor.getColumnIndex("year"))+"','"+
                                    cursor.getInt(cursor.getColumnIndex("month"))+"','"+
                                    cursor.getInt(cursor.getColumnIndex("day"))+"','"+
                                    cursor.getString(cursor.getColumnIndex("time"))+"','"+
                                    cursor.getInt(cursor.getColumnIndex("locked"))+"','"+
                                    cursor.getString(cursor.getColumnIndex("password"))+"')";
                            cnt=stmt.executeUpdate(sql);
                        } while (cursor.moveToNext());
                    }
                    EventBus.getDefault().post("2");
                }
            } catch (ClassNotFoundException e) {
                msg=e.getMessage();
                EventBus.getDefault().post("3");
            } catch (SQLException e1) {
                e1.printStackTrace();
                EventBus.getDefault().post("3");
            }
            done = true;
            Looper.loop();
        }
    };
}
