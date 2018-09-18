package com.burytomorrow.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CJT on 2018/1/8.
 */

public class DB_Helper extends SQLiteOpenHelper {

    private Context context ;

    //数据库版本
    private static final int DB_VERSION = 1 ;

    //数据库名称
    private static final String DB_NAME="Three_Kingdoms.db";

    // 构造函数
    public DB_Helper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据表
        String Create_Table =
                "CREATE TABLE Article("
                        +"id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        +"article TEXT,"
                        +"year INTEGER,"
                        +"month INTEGER,"
                        +"day INTEGER,"
                        +"time TEXT,"
                        +"locked INTEGER,"
                        +"password TEXT)" ;
        db.execSQL(Create_Table);

        // 插入数据
        ContentValues values = new ContentValues() ;
        values.put("article","<b>这是第一篇文章:我觉得做不完了</b>");
        values.put("year",2016);
        values.put("month",2);
        values.put("day",27);
        values.put("time","16:46");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","曾几何时，想要和你一起看那一段花开花落云卷云舒，只是碍于现实的悲凉，徒添一个残梦，多少镜中花，水中月，困埋在红尘烟雨中，最后消散的是梦还是现实？提着墨笔，携一喧纸，将你那婉尔一笑凝固在我心里，到了最后又是谁将你埋葬！");
        values.put("year",1999);
        values.put("month",12);
        values.put("day",30);
        values.put("time","23:59");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","这是第二篇文章:凉了呀");
        values.put("year",2016);
        values.put("month",3);
        values.put("day",19);
        values.put("time","15:48");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","人类社会前进的滚滚车轮，常会驶进一个个险峻的弯道。社会是后退还是超越，是崩溃还是繁荣，充满了变数，充满了悬念。人生的道路崎岖不平，常会步入许许多多的弯道。人生是止步还是前进，是落后还是超越，充满了挑战，充满了机遇。");
        values.put("year",2017);
        values.put("month",4);
        values.put("day",8);
        values.put("time","07:48");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","跌跌宕宕，起起伏伏，这犹如人生。只有低音与高音的糅合才得以谱写出震撼人心的乐曲；只有光明和阴影的辉映，才看得见最美丽的风景；只有悲苦和欢喜的交织，生命才会有更绚丽的色彩。");
        values.put("year",2016);
        values.put("month",3);
        values.put("day",9);
        values.put("time","15:48");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","人生百年弹指间，潮起潮落是一天，花开花谢是一季，月圆月缺是一年，生命在前行中顿悟，岁月在积累中生香。品过了颜色的厚重，便觉清新怡人；看遍了人世繁华，方觉平淡最真。一方静室，亦能修养心性；一杯清茶，亦能恬淡生香，一卷在手，安之若素。盈一份诗意于流年，嗅得阳光的清新，听得细雨的缠绵，以风的洒脱笑看沧桑；以云的飘逸轻盈过往；以花的姿态坐拥满怀阳光，用淡泊写意人生，用安然葱茏时光，让日子在材米油盐中升腾；让生活在粗茶淡饭中诗意。透过指间的光阴，淡看流年烟火，细品岁月静好，心中的风景，才是人生最美的！");
        values.put("year",2017);
        values.put("month",8);
        values.put("day",21);
        values.put("time","10:48");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","万物靠泥土孕育，人类在泥土上繁衍，泥土是我们的衣食父母，泥土平凡而朴实，博大而厚重，默默奉献而不期回报，功高盖世而绝不张扬……然而，这弥足珍贵的泥土，常常被我们忽视冷落。");
        values.put("year",2017);
        values.put("month",8);
        values.put("day",29);
        values.put("time","11:12");
        values.put("locked",0);
        db.insert("Article",null,values) ;

        values.put("article","剁手快乐");
        values.put("year",2017);
        values.put("month",11);
        values.put("day",10);
        values.put("time","23:59");
        values.put("locked",0);
        db.insert("Article",null,values) ;


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
