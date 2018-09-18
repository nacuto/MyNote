package com.burytomorrow.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Dylan on 2018/1/3.
 */

public class SelectActivity extends AppCompatActivity {
    private List<Total_text>total_texts;
    private RecyclerView select_Recyler;
    private CommonAdapter<Total_text> select_CommonAdapter;
    private RecyclerView month_Recyler;
    private CommonAdapter<Integer> month_CommonAdapter;
    TextView year,year_total;
    ImageView jiantou1,jiantou2;
    LinearLayout month_box;
    LinearLayout []m_box;
    TextView []m;
    TextView []m_total;
    boolean []flag,open,selected;
    Intent intent;
    DB_Helper db_helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);
        total_texts = new ArrayList<>();
        ImageView return_img = (ImageView)findViewById(R.id.return_img);
        TextView return_text = (TextView)findViewById(R.id.return_text);
        return_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        for(int i=0;i<12;i++){
            m = new TextView[12];
            m_total = new TextView[12];
            m_box=new LinearLayout[12];
        }

        select_Recyler = (RecyclerView)findViewById(R.id.select_recycler);
        select_Recyler.setLayoutManager(new LinearLayoutManager(this));
        get_ArticleFromDB();
        initial();

        select_Recyler.setAdapter(select_CommonAdapter);
        //Toast.makeText(this,"total_text.size="+total_texts.size(),Toast.LENGTH_LONG).show();

        final ImageView select_search = (ImageView)findViewById(R.id.select_search);
        select_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_return = new Intent(SelectActivity.this,MainActivity.class);
                ArrayList<CharSequence>YEAR = new ArrayList<CharSequence>();
                for(int i=0;i<total_texts.size();i++){
                    int iyear = total_texts.get(i).year;
                    ArrayList<CharSequence>MONTH = new ArrayList<CharSequence>();
                    boolean year_select_flag =false;
                    for(int j=0;j<12;j++){
                        if(total_texts.get(i).selected_flag[j]){
                            MONTH.add(Integer.toString(j+1));
                            year_select_flag = true;
                        }
                        Log.d("TAG",iyear+" "+j);
                    }
                    if(year_select_flag){
                        intent_return.putExtra(Integer.toString(iyear),MONTH);//根据年份存入月份数组，类型是String，
                        YEAR.add(Integer.toString(iyear));
                    }
                }
                intent_return.putCharSequenceArrayListExtra("year",YEAR);//YEAR里面存的是共选出的年份数组，类型是String类型
                setResult(6,intent_return);
                finish();
            }
        });

        final TextView desorder = (TextView)findViewById(R.id.desorder);
        final TextView order = (TextView)findViewById(R.id.order);
        desorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desorder.setVisibility(View.GONE);
                order.setVisibility(View.VISIBLE);
                Collections.sort(total_texts, new Comparator<Total_text>() {
                    @Override
                    public int compare(Total_text o1, Total_text o2) {
                        int i = o1.year - o2.year;
                        return -i;
                    }
                });
                select_CommonAdapter.notifyDataSetChanged();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setVisibility(View.GONE);
                desorder.setVisibility(View.VISIBLE);
                Collections.sort(total_texts, new Comparator<Total_text>() {
                    @Override
                    public int compare(Total_text o1, Total_text o2) {
                        int i = o1.year - o2.year;
                        return i;
                    }
                });
                select_CommonAdapter.notifyDataSetChanged();
            }
        });

    }

    void initial(){
        select_CommonAdapter = new CommonAdapter<Total_text>(this, R.layout.select_item,total_texts) {
            @Override
            public void convert(MyViewHolder holder, Total_text total_text) {
                total_text.Total_textInit();
                getViewByid(holder,total_text);
                total_text.getViewById(year,year_total,jiantou1,jiantou2,month_box,m_box,m,m_total);
                /*total_text.initial_View();*/
            }
        };
    }

    void getViewByid(MyViewHolder holder, final Total_text total_text){

        m[0] = holder.getView(R.id.m1);
        m[1] = holder.getView(R.id.m2);
        m[2] = holder.getView(R.id.m3);
        m[3] = holder.getView(R.id.m4);
        m[4] = holder.getView(R.id.m5);
        m[5] = holder.getView(R.id.m6);
        m[6] = holder.getView(R.id.m7);
        m[7] = holder.getView(R.id.m8);
        m[8] = holder.getView(R.id.m9);
        m[9] = holder.getView(R.id.m10);
        m[10] = holder.getView(R.id.m11);
        m[11] = holder.getView(R.id.m12);

        m_total[0] = holder.getView(R.id.m1_total);
        m_total[1] = holder.getView(R.id.m2_total);
        m_total[2] = holder.getView(R.id.m3_total);
        m_total[3] = holder.getView(R.id.m4_total);
        m_total[4] = holder.getView(R.id.m5_total);
        m_total[5] = holder.getView(R.id.m6_total);
        m_total[6] = holder.getView(R.id.m7_total);
        m_total[7] = holder.getView(R.id.m8_total);
        m_total[8] = holder.getView(R.id.m9_total);
        m_total[9] = holder.getView(R.id.m10_total);
        m_total[10] = holder.getView(R.id.m11_total);
        m_total[11] = holder.getView(R.id.m12_total);

        m_box[0] = holder.getView(R.id.m1_box);
        m_box[1] = holder.getView(R.id.m2_box);
        m_box[2] = holder.getView(R.id.m3_box);
        m_box[3] = holder.getView(R.id.m4_box);
        m_box[4] = holder.getView(R.id.m5_box);
        m_box[5] = holder.getView(R.id.m6_box);
        m_box[6] = holder.getView(R.id.m7_box);
        m_box[7] = holder.getView(R.id.m8_box);
        m_box[8] = holder.getView(R.id.m9_box);
        m_box[9] = holder.getView(R.id.m10_box);
        m_box[10] = holder.getView(R.id.m11_box);
        m_box[11] = holder.getView(R.id.m12_box);

        for(int i=0;i<12;i++){
            m_total[i].setText(Integer.toString(total_text.get_cnt(i)));
            if(total_text.get_cnt(i)>0){
                m[i].setTextColor(0xFF333333);
                m_total[i].setTextColor(0xFF333333);


            }else{
                m[i].setTextColor(0xFFF0F0F0);
                m_total[i].setTextColor(0xFFE0E0E0);
                m_box[i].setBackgroundResource(R.drawable.month_zero);
            }
        }
        month_box = holder.getView(R.id.month_box);
        year = holder.getView(R.id.year);
        year_total = holder.getView(R.id.year_total);
        jiantou1 = holder.getView(R.id.jiantou1);
        jiantou2 = holder.getView(R.id.jiantou2);

        year.setText(Integer.toString(total_text.year));
        year_total.setText(Integer.toString(total_text.year_total));

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_text.open_flag)
                    total_text.open_flag=false;
                else total_text.open_flag=true;
                total_text.set_month_box_View();
            }
        });
        year.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);
                builder.setMessage("是否删除"+year.getText()+"年的所有文章？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String delete_sql ="Delete from Article where year="+total_text.year;
                                db_helper.getWritableDatabase().execSQL(delete_sql);
                                total_texts.remove(total_text.index);
                                select_CommonAdapter.notifyItemRemoved(total_text.index);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return false;
            }
        });
        year_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_text.open_flag)
                    total_text.open_flag=false;
                else total_text.open_flag=true;
                total_text.set_month_box_View();
            }
        });
        jiantou1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_text.open_flag = true;
                total_text.set_month_box_View();
            }
        });
        jiantou2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_text.open_flag = false;
                total_text.set_month_box_View();
            }
        });

        for(int i=0;i<12;i++){
            final int finalI = i;
            if(total_text.get_cnt(i)>0){
                m[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(SelectActivity.this,"year="+total_text.year+" month"+finalI,Toast.LENGTH_LONG).show();
                        if(!total_text.selected_flag[finalI]){
                            total_text.selected_flag[finalI] = true;
                            total_text.set_monthView();
                        }else{
                            m_box[finalI].setBackgroundResource(R.drawable.month_init);
                            total_text.selected_flag[finalI] = false;
                            total_text.set_monthView();
                        }
                    }
                });
                m_total[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(SelectActivity.this,"year="+total_text.year+" month"+finalI,Toast.LENGTH_LONG).show();
                        if(!total_text.selected_flag[finalI]){
                            total_text.selected_flag[finalI] = true;
                            total_text.set_monthView();
                        }else{
                            m_box[finalI].setBackgroundResource(R.drawable.month_init);
                            total_text.selected_flag[finalI] = false;
                            total_text.set_monthView();
                        }
                    }
                });
            }
        }
    }


    void get_ArticleFromDB(){
        List<Integer>year_tmp = new ArrayList<>();
        List<Integer>year_total_tmp = new ArrayList<>();
        String sql1 = "Select year, Count(id) As count from Article Group by year";
        db_helper = new DB_Helper(this);
        SQLiteDatabase db = db_helper.getReadableDatabase();
        Cursor cursor1 = db.rawQuery(sql1,null);

        if(cursor1.moveToFirst()){
            do{
                int year_tmp1 = cursor1.getInt(cursor1.getColumnIndex("year"));
                int year_total_tmp1 = cursor1.getInt(cursor1.getColumnIndex("count"));
                year_tmp.add(year_tmp1);
                year_total_tmp.add(year_total_tmp1);
//                Log.d("TAG", Integer.toString(year_tmp1));
//                Log.d("TAG", Integer.toString(year_total_tmp1));
            }
            while(cursor1.moveToNext());
        }
        int index=0;
        for(int i=0;i<year_tmp.size();i++){
            Total_text total_text_tmp = new Total_text();
            total_text_tmp.index = index;
            index++;
            int year_tmp1 = year_tmp.get(i);
            int year_total_tmp1 = year_total_tmp.get(i);
            int []cnt_tmp={0,0,0,0,0,0,0,0,0,0,0,0};
            String sql2 = "Select month, Count(id) As count from Article where year="+year_tmp1+" Group by month ";
            Cursor cursor2 = db.rawQuery(sql2,null);
            if(cursor2.moveToFirst()){
                do{
                    int month_tmp1 = cursor2.getInt(cursor2.getColumnIndex("month"));
                    int count_tmp = cursor2.getInt(cursor2.getColumnIndex("count"));

                    cnt_tmp[month_tmp1-1]=count_tmp;
                }
                while(cursor2.moveToNext());
            }
            total_text_tmp.set_cnt(year_tmp1, year_total_tmp1, cnt_tmp);
            total_texts.add(total_text_tmp);
        }

        //按照年份从大到小的顺序
        Collections.sort(total_texts, new Comparator<Total_text>() {
            @Override
            public int compare(Total_text o1, Total_text o2) {
                int i = o1.year - o2.year;
                return i;
            }
        });
    }

    void Deliever_Selected(){

    }
}
