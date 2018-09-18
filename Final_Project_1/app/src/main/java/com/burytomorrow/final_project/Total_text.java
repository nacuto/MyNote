package com.burytomorrow.final_project;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Dylan on 2018/1/7.
 */

public class Total_text {//存储有文章的年份
    private int[] cnt={0,0,0,0,0,0,0,0,0,0,0,0};
    public int year_total=0;
    public int year=0;
    public int index;
    public boolean []selected_flag;//判断月份是否被选中
    public boolean open_flag;//判断年列表是否被打开
    private TextView y,y_total;//显示年份，年份文章数
    private ImageView jiantou1,jiantou2;//右箭头，下箭头
    private LinearLayout month_box;//月份列表显示
    private LinearLayout []m_box;//设置月份被选中后的背景
    private TextView []m;//月份
    private TextView []m_total;//月份文章数

    void Total_textInit(){
        m_box = new LinearLayout[12];
        m = new TextView[12];
        m_total = new TextView[12];
        selected_flag =new boolean[12];

    }

    void set_cnt(int year, int year_total, int []m_total ){
        //根据数据库设置---------------未完成
        for(int i=0;i<12;i++){
            this.cnt[i]=m_total[i];
        }
        this.year_total=year_total;
        this.year = year;
        open_flag = false;
    }

    void getViewById(TextView y, TextView y_total, ImageView jiantou1, ImageView jiantou2, LinearLayout month_box, LinearLayout[]m_box, TextView[]m, TextView[]m_total){
        for(int i=0;i<12;i++){
            selected_flag[i]=false;
            this.m_box[i] = m_box[i];
            this.m[i] = m[i];
            this.m_total[i] = m_total[i];
        }
        this.y = y;
        this.y_total = y_total;
        this.jiantou1 = jiantou1;
        this.jiantou2 = jiantou2;
        this.month_box = month_box;
    }

    void initial_View(){
        y.setText(year);
        y_total.setText(year_total);
        for(int i=0;i<12;i++){
            m_total[i].setText(cnt[i]);
            if(cnt[i]>0){
                m[i].setTextColor(0xFF000000);///设定月份和月份文章数的颜色，黑色---可以点击
                m_total[i].setTextColor(0xFF000000);
            }else{
                m[i].setTextColor(0xFFF0F0F0);//设定月份和月份文章数的颜色，灰色---不能点击
                m_total[i].setTextColor(0xFFF0F0F0);
            }
        }
        jiantou1.setVisibility(View.VISIBLE);
        jiantou2.setVisibility(View.GONE);
        month_box.setVisibility(View.GONE);
    }
    void set_month_box_View(){
        TranslateAnimation mShowAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(250);

        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(250);
        if(open_flag){
            month_box.startAnimation(mShowAction);
            month_box.setVisibility(View.VISIBLE);
            jiantou1.setVisibility(View.GONE);
            jiantou2.setVisibility(View.VISIBLE);
        }else{
            month_box.startAnimation(mHiddenAction);
            month_box.setVisibility(View.GONE);
            jiantou2.setVisibility(View.GONE);
            jiantou1.setVisibility(View.VISIBLE);
        }
    }

    void set_monthView(){
        for(int i=0;i<12;i++){
            if(cnt[i]>0){
                if(selected_flag[i]){
                    m_box[i].setBackgroundResource(R.drawable.month_selected);
//                m[i].setTextColor(0xFFC0F0F0);
//                m_total[i].setTextColor(0xFFC0F0F0);
                }else{
                    m_box[i].setBackgroundResource(R.drawable.month_init);
//                m[i].setTextColor(0xFFF0F0F0);
//                m_total[i].setTextColor(0xFFF0F0F0);
                }
            }
        }
    }

    int get_cnt(int i){
        return cnt[i];
    }

    boolean get_selectedflag(int i){
        return selected_flag[i];
    }


}

