package com.burytomorrow.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class MainActivity extends AppCompatActivity {
//主界面控件定义
    //标题栏控件
    private Button cancelButton;
    private ImageButton selectTimeButton;
    private TextView titleText;
    private Button selectAllButton;
    private ImageButton modeButton;
    private LinearLayout titleLayout;
    private boolean mode = false;//mode表示列表模式还是宫格模式，true的时候代表宫格模式
    //搜索栏控件
    private LinearLayout searchLinearLayout;
    private EditText searchText;
    private String LastSearchText = "";
    //列表控件
    private LinearLayout mBar;//隐藏的进度条
    private LinearLayout mList;//列表框
    private RecyclerView mRecycler;
    private CommonAdapter<Article> mCommonAdapter;//自定义列表适配器
    private List<Article> articles = null;//所有需要显示的文章
    private PopupWindow mPopupWindow;//下拉窗口
    private int Width;//手机宽度
    private int Height;//手机高度
    //隐藏菜单控件
    private boolean special = false;//是否进入了特殊模式，也就是通过长按单项进入删除或者其他操作的模式
    private boolean isSelectAll = false;//是否全选了
    private int selectCount = 0;//选择了多少项
    private LinearLayout theHideMenu;
    private ImageButton deleteButton;
    private ImageButton lockButton;
    private ImageButton alarmButton;
    AlertDialog.Builder builder;//弹出框
    private SharedPreferences sharedPreferences;
    //悬浮按钮控件
    private FloatingActionButton toTopButton;
    private FloatingActionButton addButton;

    // 本地数据库
    private DB_Helper db_helper =new DB_Helper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager wm = this.getWindowManager();
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();

        //eventBus注册
        EventBus.getDefault().register(this);
        //初始化数据
        initData();
        //初始化控件
        findView();
        //初始化recyclerView
        initRecyclerView();
        //按键监听事件
        listener();
        //运行子线程监听输入框
        runHandler();

    }
    //初始化数据
    public void initData(){
        if(articles!=null) articles.clear();
        articles = new ArrayList<>();
        SQLiteDatabase db  = db_helper.getReadableDatabase() ;
        Cursor cursor = db.rawQuery("SELECT * FROM Article", null);
        if (cursor.moveToFirst()) {
            do {
                //添加人物信息
                articles.add(
                        new Article(
                                cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("article")),
                                cursor.getInt(cursor.getColumnIndex("year")),
                                cursor.getInt(cursor.getColumnIndex("month")),
                                cursor.getInt(cursor.getColumnIndex("day")),
                                cursor.getString(cursor.getColumnIndex("time")),
                                cursor.getInt(cursor.getColumnIndex("locked")),
                                cursor.getString(cursor.getColumnIndex("password"))
                        )
                );
            } while (cursor.moveToNext());
        }
    }
    //重新初始化列表数据
    public void reInitData(){
        LastSearchText = searchText.getText().toString();
        if(LastSearchText.equals("")){
            initData();
            initRecyclerView();
        }else{
            articles.clear();
            articles = new ArrayList<>();
            SQLiteDatabase db  = db_helper.getReadableDatabase() ;
            Cursor cursor = db.rawQuery("SELECT * FROM Article where article like '%"+LastSearchText+ "%'", null);
            if (cursor.moveToFirst()) {
                do {
                    //添加人物信息
                    articles.add(
                            new Article(
                                    cursor.getInt(cursor.getColumnIndex("id")),
                                    cursor.getString(cursor.getColumnIndex("article")),
                                    cursor.getInt(cursor.getColumnIndex("year")),
                                    cursor.getInt(cursor.getColumnIndex("month")),
                                    cursor.getInt(cursor.getColumnIndex("day")),
                                    cursor.getString(cursor.getColumnIndex("time")),
                                    cursor.getInt(cursor.getColumnIndex("locked")),
                                    cursor.getString(cursor.getColumnIndex("password"))
                            )
                    );
                } while (cursor.moveToNext());
            }
            initRecyclerView();
        }
    }
    //初始化控件
    public void findView(){
        //标题栏控件
        cancelButton = (Button) findViewById(R.id.main_cancel);
        selectTimeButton = (ImageButton) findViewById(R.id.main_search_time);
        titleText = (TextView) findViewById(R.id.main_title);
        selectAllButton = (Button) findViewById(R.id.main_select_all);
        modeButton = (ImageButton) findViewById(R.id.main_mode);
        titleLayout = (LinearLayout) findViewById(R.id.linearLayout);
        //搜索栏控件
        searchLinearLayout = (LinearLayout) findViewById(R.id.linearLayout2);
        searchText = (EditText) findViewById(R.id.main_search_text);
        //列表控件
        mBar = (LinearLayout) findViewById(R.id.main_progressbar);
        mList = (LinearLayout) findViewById(R.id.list_layout);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        //隐藏菜单控件
        theHideMenu = (LinearLayout) findViewById(R.id.hidemenu);
        deleteButton = (ImageButton) findViewById(R.id.main_delete);
        lockButton = (ImageButton) findViewById(R.id.main_lock);
        //悬浮按钮控件
        toTopButton = (FloatingActionButton) findViewById(R.id.main_to_top);
        addButton = (FloatingActionButton) findViewById(R.id.main_add);
    }
    //初始化弹出框
    void initDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("删除后无法恢复，是否确认删除这"+selectCount+"项？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "请重新进行操作", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < articles.size(); ) {
                            if (articles.get(i).check) { //删除项
                                mDelete(articles.get(i).articleID);
                                articles.remove(i);
                                mCommonAdapter.notifyItemRemoved(i);
                            } else i++;
                        }
                        hiedMenu();
                    }
                });
    }
    //初始化recyclerView
    public void initRecyclerView(){
        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                int i = o1.year - o2.year;
                if(i==0){
                    i = o1.month - o2.month;
                    if(i == 0){
                        i=o1.day-o2.day;
                    }
                }
                return i;
            }
        });
        if(mode){//如果是宫格模式
            mRecycler.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            mCommonAdapter = new CommonAdapter<Article>(this,R.layout.gongge_recycler,articles) {
                @Override
                public void convert(MyViewHolder holder, Article article) {
                    TextView g_article = holder.getView(R.id.gongge_article);
                    TextView g_time = holder.getView(R.id.gongge_time);
                    ImageView g_lock = holder.getView(R.id.gongge_lock);
                    if(article.lock){
                        g_lock.setVisibility(View.VISIBLE);
                        g_article.setMaxLines(1);
                    } else {
                        g_lock.setVisibility(View.GONE);
                    }
                    String time = article.year+" 年 "+ article.month + " 月 "+ article.day +" 日 "+ article.time;
                    g_article.setText(Html.fromHtml(article.article));
                    g_time.setText(time);
                    LinearLayout linearLayout = holder.getView(R.id.gongge_hideLayout);
                    if(special){ //如果进入了特殊模式，则显示相应控件
                        linearLayout.setVisibility(View.VISIBLE);
                        ImageView ischeck = holder.getView(R.id.gongge_ischeck);
                        ImageView uncheck = holder.getView(R.id.gongge_uncheck);
                        if(article.check){ //如果被选中了
                            linearLayout.setBackgroundColor(Color.parseColor("#40938192"));
                            ischeck.setVisibility(View.VISIBLE);
                            uncheck.setVisibility(View.GONE);
                        }else{
                            linearLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                            ischeck.setVisibility(View.GONE);
                            uncheck.setVisibility(View.VISIBLE);
                        }
                    }else{ //否则就显示正常信息
                        linearLayout.setVisibility(View.GONE);
                    }
                }
            };
        }else{//如果是列表模式
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mCommonAdapter = new CommonAdapter<Article>(this,R.layout.list_recycler,articles) {
                @Override
                public void convert(MyViewHolder holder, Article article) {
                    TextView l_article = holder.getView(R.id.list_article);
                    TextView l_time = holder.getView(R.id.list_time);
                    ImageView l_lock = holder.getView(R.id.list_lock);
                    LinearLayout set = holder.getView(R.id.setAlpha);
                    //set.getBackground().setAlpha(120);
                    if(article.lock){
                        l_lock.setVisibility(View.VISIBLE);
                        l_article.setMaxLines(1);
                    }
                    else l_lock.setVisibility(View.GONE);
                    String time = article.year+" 年 "+ article.month + " 月 "+ article.day +" 日 "+ article.time;
                    l_article.setText(Html.fromHtml(article.article));
                    l_time.setText(time);
                    LinearLayout linearLayout = holder.getView(R.id.list_hideLayout);
                    if(special){ //如果进入了特殊模式，则显示相应控件
                        linearLayout.setVisibility(View.VISIBLE);
                        ImageView ischeck = holder.getView(R.id.list_ischeck);
                        ImageView uncheck = holder.getView(R.id.list_uncheck);
                        if(article.check){ //如果被选中了
                            //linearLayout.setBackgroundColor(Color.parseColor("#40938192"));
                            ischeck.setVisibility(View.VISIBLE);
                            uncheck.setVisibility(View.GONE);
                        }else{
                            linearLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                            ischeck.setVisibility(View.GONE);
                            uncheck.setVisibility(View.VISIBLE);
                        }
                    }else{ //否则就显示正常信息
                        linearLayout.setVisibility(View.GONE);
                    }
                }
            };
        }
        //设置动画加载效果
        ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(mCommonAdapter);
        animationAdapter.setDuration(50);
        mRecycler.setAdapter(animationAdapter);
        mRecycler.setItemAnimator(new OvershootInLeftAnimator());
        //列表点击事件
        recyclerListener();
        //滑动事件
        scrollListener();
    }
    //隐藏主界面下方菜单
    public void hiedMenu(){
        special = false;
        cancelButton.setVisibility(View.GONE);
        selectTimeButton.setVisibility(View.VISIBLE);
        titleText.setText("笺");
        selectAllButton.setVisibility(View.GONE);
        modeButton.setVisibility(View.VISIBLE);
        searchLinearLayout.setVisibility(View.VISIBLE);
        theHideMenu.setVisibility(View.GONE);
        selectCount=0;
        isSelectAll = false;//初始化全选按钮
        selectAllButton.setText("全选");
        addButton.setVisibility(View.VISIBLE);
        mCommonAdapter.notifyItemRangeChanged(0, articles.size());//更新列表
    }
    //显示主界面下方菜单
    public void showMenu(){
        special = true;
        cancelButton.setVisibility(View.VISIBLE);
        selectTimeButton.setVisibility(View.GONE);
        if(selectCount==0) titleText.setText("请选择项目");
        else titleText.setText("已选择了"+selectCount+"项");
        selectAllButton.setVisibility(View.VISIBLE);
        modeButton.setVisibility(View.GONE);
        searchLinearLayout.setVisibility(View.GONE);
        theHideMenu.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.INVISIBLE);
        mCommonAdapter.notifyItemRangeChanged(0, articles.size());//更新列表
        lockButton.getBackground().setAlpha(255);
    }
    //监听事件
    public void listener(){
        //标题栏点击事件
        headListener();
        //主界面下方隐藏菜单触发事件
        hideMenuListener();
        //悬浮按钮触发事件
        floatingButtonListener();
        //去除输入法的触发事件
        hideSoftInput();
    }
    //标题栏点击事件，特殊模式指长按进入的隐藏菜单操作模式
    public void headListener(){
        //非特殊模式下的日期筛选按钮点击事件
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent select_intent = new Intent(MainActivity.this,SelectActivity.class);
//                startActivityForResult(select_intent,5);
                settingPopUp();
            }
        });
        //非特殊模式下的模式切换按钮点击事件
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode){//如果是宫格模式的话，就换成列表模式
                    mode = false;
                    modeButton.setBackgroundResource(R.mipmap.gongge);
                    initRecyclerView();
                }else{//如果是列表模式的话，就换成宫格模式
                    mode = true;
                    modeButton.setBackgroundResource(R.mipmap.liebiao);
                    initRecyclerView();
                }
            }
        });
        //特殊模式下取消按钮
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < articles.size(); i++) {
                    articles.get(i).check = false;
                }
                hiedMenu(); //退出特殊模式并隐藏菜单
            }
        });
        //特殊模式下全选按钮
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelectAll){ //如果已经全选了，则变为全不选
                    isSelectAll = false;
                    selectAllButton.setText("全选");
                    for (int i = 0; i < articles.size(); i++) {
                        articles.get(i).check = false;
                    }
                    selectCount = 0;
                    titleText.setText("请选择项目");
                }else{
                    isSelectAll = true;
                    selectAllButton.setText("全不选");
                    for (int i = 0; i < articles.size(); i++) {
                        articles.get(i).check = true;
                    }
                    selectCount = articles.size();
                    titleText.setText("已选择"+selectCount+"项");
                }
                mCommonAdapter.notifyItemRangeChanged(0, articles.size());//更新列表
            }
        });
    }
    //通过子线程来处理搜索框输入事件
    public void runHandler(){
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case 123:
                        //检测输入的值，如果和上次保留的不一致，则更新列表
                        if(!searchText.getText().toString().equals(LastSearchText)){
                            //Toast.makeText(MainActivity.this, "更新列表 "+searchText.getText().toString(), Toast.LENGTH_SHORT).show();
                            reInitData();
                        }
                        break;
                }
            }
        };

        Thread mThread = new Thread(){
            @Override
            public void run(){
                while(true){
                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    mHandler.obtainMessage(123).sendToTarget();
                }
            }
        };
        mThread.start();
    }
    //列表点击事件
    public void recyclerListener(){
        mCommonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if(special){ //如果是在特殊模式下
                    if(articles.get(position).check){ //如果本来就被选中
                        articles.get(position).check = false;
                        selectCount = selectCount - 1;
                        if(selectCount < 0 ) selectCount = 0;
                    }else{
                        articles.get(position).check = true;
                        selectCount = selectCount + 1;
                    }
                    if(selectCount==0) titleText.setText("请选择项目");
                    else titleText.setText("已选择了"+selectCount+"项");
                    mCommonAdapter.notifyItemRangeChanged(position, 1);//更新item
                }else{ //否则的话点击就进入详细信息界面
                    if(articles.get(position).lock){ //如果加锁，则输入密码
                        popupWindow(3,position);
                    }else{ //跳转
                        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                        intent.putExtra("id",articles.get(position).articleID);
                        startActivityForResult(intent, 1);
                    }
                }
            }
            @Override
            public void onLongClick(int position) {
                if(!special){
                    showMenu();
                }
            }
        });
    }
    //设置按钮的下拉框设置
    public void settingPopUp(){
        View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.settingpopup, null);
        //为PopupWindow指定宽度和高度
        mPopupWindow = new PopupWindow(popupView, 263, 443);
        //设置背景颜色
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        //设置可以获取焦点
        mPopupWindow.setFocusable(true);
        //设置可以触摸弹出框以外的区域
        mPopupWindow.setOutsideTouchable(true);
        //触摸popupwindow以外区域时使其消失
        //popupwindow状态更新
        mPopupWindow.update();
        //以下拉的方式显示，并且可以设置显示的位置
        mPopupWindow.showAsDropDown(selectTimeButton,-27,40);

        Button popUpSearchTime = (Button) popupView.findViewById(R.id.pop_search_time);
        popUpSearchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                Intent select_intent = new Intent(MainActivity.this,SelectActivity.class);
                startActivityForResult(select_intent,5);
            }
        });
        Button popUpToCloud = (Button) popupView.findViewById(R.id.pop_to_cloud);
        popUpToCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mBar.setVisibility(View.VISIBLE);
                mList.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this,DBActivity.class);
                intent.putExtra("type","to");
                startActivity(intent);
            }
        });
        Button popUpFromCloud = (Button) popupView.findViewById(R.id.pop_from_cloud);
        popUpFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                mBar.setVisibility(View.VISIBLE);
                mList.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this,DBActivity.class);
                intent.putExtra("type","from");
                startActivity(intent);
            }
        });
    }
    //下拉窗口
    public void popupWindow(final int popMode , final int position) {
        View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.popupview, null);
        //为PopupWindow指定宽度和高度
        mPopupWindow = new PopupWindow(popupView, Width, 2*Height / 5);
        //设置动画
        mPopupWindow.setAnimationStyle(R.style.popup_window_anim);
        //设置背景颜色
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        //设置可以获取焦点
        mPopupWindow.setFocusable(true);
        //设置可以触摸弹出框以外的区域
        mPopupWindow.setOutsideTouchable(true);
        //触摸popupwindow以外区域时使其消失
        //popupwindow状态更新
        mPopupWindow.update();
        //以下拉的方式显示，并且可以设置显示的位置
        mPopupWindow.showAsDropDown(titleLayout);

        //下拉窗口的findViewById:
        final EditText popUpNewPW = (EditText) popupView.findViewById(R.id.pop_new_pw);
        final EditText popUpConfPW = (EditText) popupView.findViewById(R.id.pop_conf_pw);
        final EditText popUpPW = (EditText) popupView.findViewById(R.id.pop_pw);
        Button popUpOK = (Button) popupView.findViewById(R.id.pop_ok);
        Button popUpCancel = (Button) popupView.findViewById(R.id.pop_cancel);
        //初始化显示
        if(popMode == 1 || popMode == 3 || popMode == 4){ //如果是修改密码或输入密码跳转界面
            popUpNewPW.setVisibility(View.GONE);
            popUpConfPW.setVisibility(View.GONE);
            popUpPW.setVisibility(View.VISIBLE);
        }else if (popMode==2){ //如果是新增密码
            popUpNewPW.setVisibility(View.VISIBLE);
            popUpConfPW.setVisibility(View.VISIBLE);
            popUpPW.setVisibility(View.GONE);
        }
        //两个按钮的监听事件
        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        //点击OK的触发事件
        popUpOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences=getSharedPreferences("Encryption",MODE_PRIVATE);
                if(popMode == 1){ //修改密码
                    int position = 0;
                    for(int i = 0 ;i<articles.size();i++){
                        if(articles.get(i).check) position = i;
                    }
                    String pw = articles.get(position).password;
                    String inputPW = popUpPW.getText().toString();
                    if(pw.equals(MD5Utils.md5Password(inputPW))){ //加密密码相同，则修改密码
                        mPopupWindow.dismiss();
                        popupWindow(2, 0);
                    }else{
                        Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }else if ( popMode == 2){ //输入新密码
                    String newPW = popUpNewPW.getText().toString();
                    String confPW = popUpConfPW.getText().toString();
                    if(newPW.isEmpty()){ // 密码不能为空
                        Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                    else if(!newPW.equals(confPW)){//密码不匹配
                        Toast.makeText(MainActivity.this, "两次输入密码不匹配，请重新确认", Toast.LENGTH_SHORT).show();
                    }
                    else{ //开始加密
                        for(int i=0;i<articles.size();i++){
                            if(articles.get(i).check){
                                articles.get(i).lock = true;
                                articles.get(i).password = MD5Utils.md5Password(newPW);
                                mUpdate(articles.get(i));
                                articles.get(i).check = false;
                            }
                        }
                        Toast.makeText(MainActivity.this, "加密成功", Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        hiedMenu();
                    }
                }else if ( popMode == 3){ //输入密码跳转
                    String pw = articles.get(position).password;
                    String inputPW = popUpPW.getText().toString();
                    if(pw.equals(MD5Utils.md5Password(inputPW))){ //加密密码相同，则跳转
                        //跳转界面
                        Toast.makeText(MainActivity.this, "密码正确"+pw, Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                        intent.putExtra("id",articles.get(position).articleID);
                        startActivityForResult(intent, 1);
                    }else{
                        Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }else if(popMode == 4){ //输入密码然后删除
                    int p = 0;
                    for(int i=0;i<articles.size();i++){
                        if(articles.get(i).check){
                            p=i;
                            break;
                        }
                    }
                    String pw = articles.get(p).password;
                    String inputPW = popUpPW.getText().toString();
                    if(pw.equals(MD5Utils.md5Password(inputPW))){ //加密密码相同，则删除
                        Toast.makeText(MainActivity.this, "密码正确"+pw, Toast.LENGTH_SHORT).show();
                        mPopupWindow.dismiss();
                        initDialog();
                        builder.create().show();
                    }else{
                        Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    //列表滑动事件
    public void scrollListener(){
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager mlayoutManager = mRecycler.getLayoutManager();
                int visibleItemCount = mlayoutManager.getChildCount();//获取屏幕内的list数量
                int totallItemCount = mlayoutManager.getItemCount();//获取列表总数量
                int pastVisiblesItems =0;
                if(mlayoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mlayoutManager;
                    pastVisiblesItems = layoutManager.findLastVisibleItemPosition();
                }
                else if (mlayoutManager instanceof StaggeredGridLayoutManager){
                    int[] lastPositions = new int[((StaggeredGridLayoutManager) mlayoutManager).getSpanCount()];
                    ((StaggeredGridLayoutManager) mlayoutManager).findLastVisibleItemPositions(lastPositions);
                    for(int i=0;i<lastPositions.length;i++){
                        if(lastPositions[i]>pastVisiblesItems) pastVisiblesItems = lastPositions[i];
                    }
                }
                if(pastVisiblesItems>visibleItemCount){
                    toTopButton.setVisibility(View.VISIBLE);
                }else{
                    toTopButton.setVisibility(View.GONE);
                }
            }
        });
    }
    //主界面下方隐藏菜单触发事件
    public void hideMenuListener(){
        //删除按钮
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                //确保所选的全部都还没加锁
                for(int i=0;i<articles.size();i++){
                    if(articles.get(i).check && articles.get(i).lock){
                        flag = false;
                    }
                }
                if(selectCount>1&&flag == false){
                    Toast.makeText(MainActivity.this, "所选项内存在部分已设密码，请对其单独进行操作", Toast.LENGTH_SHORT).show();
                }else if(selectCount == 1 && flag == false){
                    popupWindow(4,0);
                }else if(selectCount>0&&flag){
                    initDialog();
                    builder.create().show();
                }
            }
        });
        //上锁按钮
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "";
                boolean flag = true;
                //确保所选的全部都还没加锁
                for(int i=0;i<articles.size();i++){
                    if(articles.get(i).check && articles.get(i).lock){
                        flag = false;
                    }
                }
                if(selectCount>1 && flag == false){
                    Toast.makeText(MainActivity.this, "所选项内存在部分已设密码，请对其单独进行操作", Toast.LENGTH_SHORT).show();
                }else if ( selectCount == 1 && flag == false){
                    popupWindow(1 , 0); //修改密码
                }else if ( selectCount > 0 && flag){
                    popupWindow(2 , 0); //设置密码
                }
            }
        });
    }
    //悬浮按钮触发事件
    public void floatingButtonListener(){
        //返回顶部按钮
        toTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecycler.smoothScrollToPosition(0);
            }
        });
        //新增按钮
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("id",-1);
                startActivityForResult(intent, 1);
            }
        });
    }
    //去除输入法的触发事件
    public void hideSoftInput(){
        titleLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                titleLayout.setFocusable(true);
                titleLayout.setFocusableInTouchMode(true);
                titleLayout.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleLayout.getWindowToken(), 0);
                return false;
            }
        });
        mRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRecycler.setFocusable(true);
                mRecycler.setFocusableInTouchMode(true);
                mRecycler.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mRecycler.getWindowToken(), 0);
                return false;
            }
        });
    }
    //数据库删除操作
    void mDelete(int s) {
        db_helper.getWritableDatabase().execSQL("DELETE FROM Article WHERE id =" + s + " ");
    }
    //数据库更新操作
    void mUpdate(Article p) {
        String sql = "UPDATE Article set password='" + p.password + "' ,locked = "+1+" WHERE id= " + p.articleID ;
        db_helper.getWritableDatabase().execSQL(sql);
        int position = 0;
        for (int i = 0; i < articles.size(); i++) {
            if (articles.get(i).articleID == p.articleID) {
                position = i;
                break;
            }
        }
        mCommonAdapter.notifyItemChanged(position);
    }
    //EventBus事件处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String a){
        mBar.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
        if(a.equals("1")){
            Toast.makeText(MainActivity.this,"数据已从云端拉取成功！",Toast.LENGTH_SHORT).show();
            for(int i=0;i<1000000;i++);
            reInitData();
        }
        else if ( a.equals("2") ){
            Toast.makeText(MainActivity.this,"数据已同步到云端！",Toast.LENGTH_SHORT).show();
        }
        else if ( a.equals("3") ){
            Toast.makeText(MainActivity.this,"同步失败，请检查网络！",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    //重写返回键的操作
    @Override
    public void onBackPressed(){
        if(special){ //如果是在特殊模式下点的返回键
            for (int i = 0; i < articles.size(); i++) {
                articles.get(i).check = false;
            }
            hiedMenu(); //退出特殊模式并隐藏菜单
        }else{
            finish();
        }
    }
    // 返回别的activity传回的东西
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            reInitData();
        } else if (resultCode == 6) {
            int count = 0;
            SQLiteDatabase db  = db_helper.getReadableDatabase() ;
            Cursor cursor = db.rawQuery("SELECT * FROM Article", null);
            List<Article> temp_article = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    //添加人物信息
                    temp_article.add(
                            new Article(
                                    cursor.getInt(cursor.getColumnIndex("id")),
                                    cursor.getString(cursor.getColumnIndex("article")),
                                    cursor.getInt(cursor.getColumnIndex("year")),
                                    cursor.getInt(cursor.getColumnIndex("month")),
                                    cursor.getInt(cursor.getColumnIndex("day")),
                                    cursor.getString(cursor.getColumnIndex("time")),
                                    cursor.getInt(cursor.getColumnIndex("locked")),
                                    cursor.getString(cursor.getColumnIndex("password"))
                            )
                    );
                } while (cursor.moveToNext());
            }
            articles.clear();
            ArrayList<CharSequence>YEAR = data.getExtras().getCharSequenceArrayList("year");
            for(int i=0;i<YEAR.size();i++){
                CharSequence temp = YEAR.get(i);
                int a = Integer.parseInt((String) YEAR.get(i));
                ArrayList<CharSequence>MONTH = data.getExtras().getCharSequenceArrayList(temp.toString());
                for(int j=0;j<MONTH.size();j++){
                    int b = Integer.parseInt((String) MONTH.get(j));
                    for(int k=0;k<temp_article.size();k++){
                        if(temp_article.get(k).year == a&&temp_article.get(k).month==b){
                            articles.add(temp_article.get(k));
                            count++;
                        }
                    }
                }
            }
            if(count == 0) reInitData();
            initRecyclerView();
            searchText.setText("");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
