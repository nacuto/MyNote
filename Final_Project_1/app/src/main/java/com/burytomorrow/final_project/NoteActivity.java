package com.burytomorrow.final_project;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import jp.wasabeef.richeditor.RichEditor;

public class NoteActivity extends AppCompatActivity {

    public RichEditor richEditor ;
    public TextView htmlCodeTV,dateTV,timeTV ;
    public String htmlCode,date,time,pass="" ;

    public LinearLayout noteTool ;
    public HorizontalScrollView editTool ;

    public boolean ifSetCenter ;

    public String fileBasePath,filePath;

    public Dialog pictureMenu ;
    public Button cameraBtn,albumBtn ;
    public Button lockBtn ;

    public Uri uriTempFile,imageUri ;

    public DB_Helper db_helper = new DB_Helper(this);
    public int id=3 ;

    // 申请权限
    void verifyPermission(){
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this,"android.permission.READ_EXTERNAL_STORAGE");
        if ( permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE,1);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    void init(){
        richEditor = (RichEditor) findViewById(R.id.editText);
        // 设置富文本初始化格式
        richEditor.setBackgroundColor(Color.parseColor("#FAFAFA"));
        richEditor.setEditorFontSize(20);  // px
        richEditor.setPadding(20, 10, 30, 0); // left, top, right, bottom

        htmlCodeTV = (TextView) findViewById(R.id.htmlCode) ;
        dateTV = (TextView) findViewById(R.id.dayTV) ;
        timeTV = (TextView) findViewById(R.id.timeTV) ;
        ifSetCenter = false ;

        //保存图片的路径
        fileBasePath= Environment.getExternalStorageDirectory().
                getAbsolutePath()+"/finalProject/";

        noteTool = (LinearLayout) findViewById(R.id.NoteTool) ;
        editTool = (HorizontalScrollView) findViewById(R.id.EditTool) ;

        // 设置lock图片半透明
        lockBtn = (Button) findViewById(R.id.lockBtn) ;
        lockBtn.getBackground().setAlpha(120);
    }
    // 接收从MainActivity传过来的信息
    void getData(){
//        if ( getIntent().getExtras()!=null ){
            id = getIntent().getExtras().getInt("id") ;
            if ( id>0 ){
                SQLiteDatabase db = db_helper.getReadableDatabase() ;
                Cursor cursor = db.rawQuery("SELECT * FROM Article WHERE id="+id,null) ;
                cursor.moveToFirst() ;
//                Toast.makeText(this,cursor.getString(cursor.getColumnIndex("article")),Toast.LENGTH_SHORT).show();
                htmlCode = cursor.getString(cursor.getColumnIndex("article")) ;
                date = cursor.getString(cursor.getColumnIndex("year"))+"年"
                        +cursor.getString(cursor.getColumnIndex("month"))+"月"
                        + cursor.getString(cursor.getColumnIndex("day"))+"日" ;
                dateTV.setText(date);
                time = cursor.getString(cursor.getColumnIndex("time")) ;
                timeTV.setText(time);
                richEditor.setHtml(htmlCode);
            }
            else{
                Calendar calendar=Calendar.getInstance();
                date = calendar.get(Calendar.YEAR)+"年"
                        +calendar.get(Calendar.MONTH)+1+"月"
                        +calendar.get(Calendar.DAY_OF_MONTH)+"日" ;
                dateTV.setText(date);
                time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) ;
                timeTV.setText(time);
            }
//        }


    }

    // 软键盘弹出收起的监听响应
    void softKeyBoardListener(){
        SoftKeyBoardListener.setListener(NoteActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
//                Toast.makeText(NoteActivity.this,"键盘显示",Toast.LENGTH_SHORT).show();
                editTool.setVisibility(View.VISIBLE);
                noteTool.setVisibility(View.GONE);
            }

            @Override
            public void keyBoardHide(int height) {
//                Toast.makeText(NoteActivity.this,"键盘隐藏",Toast.LENGTH_SHORT).show();
                richEditor.clearFocus() ;
                editTool.setVisibility(View.GONE);
                noteTool.setVisibility(View.VISIBLE);
            }
        });
    }
    // 富文本编辑时Tool按键的响应
    void editToolResponse(){
        // html代码的显示
//        richEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
//            @Override
//            public void onTextChange(String text) {
//                htmlCodeTV.setText(text);
//            }
//        });

        findViewById(R.id.pictureBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPictureMenuDialog();
            }
        });
        findViewById(R.id.centerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !ifSetCenter ){
                    richEditor.setAlignCenter();
                    ifSetCenter = true ;
                }
                else {
                    richEditor.setAlignLeft();
                    ifSetCenter = false ;
                }

            }
        });
        findViewById(R.id.boldBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditor.setBold();
            }
        });
        findViewById(R.id.italicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditor.setItalic();
            }
        });
        findViewById(R.id.biggerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditor.setStrikeThrough();
            }
        });
        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditor.undo();
            }
        });

    }
    // 便签Tool按键的响应
    void noteToolResponse(){
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( id>0 ){
                    String sql = "UPDATE Article set article='" + richEditor.getHtml() + "'" ;
                    if ( !pass.equals("") ) sql += ", locked="+1+", password='"+pass+"'" ;
                    sql += " WHERE id="+id ;
                    db_helper.getWritableDatabase().execSQL(sql);
                }
                else{
                    Calendar calendar=Calendar.getInstance();
                    ContentValues values = new ContentValues() ;
                    values.put("article",richEditor.getHtml());
                    values.put("year",calendar.get(Calendar.YEAR));
                    values.put("month",calendar.get(Calendar.MONTH)+1);
                    values.put("day",calendar.get(Calendar.DAY_OF_MONTH));
                    values.put("time",time);
                    if ( pass.equals("") ) values.put("locked",0);
                    else{
                        values.put("locked",1);
                        values.put("password",pass);
                    }
                    db_helper.getWritableDatabase().insert("Article",null,values) ;
                }
                finish() ;
            }
        });

        findViewById(R.id.shareBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, richEditor.getHtml());
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent,"分享给"));
            }
        });
        findViewById(R.id.delBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NoteActivity.this);
                alertDialog.setTitle("是否删除")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if( id>0 ){
                                    db_helper.getWritableDatabase().execSQL("DELETE FROM Article WHERE id =" + id + " ");
//                                    Toast.makeText(NoteActivity.this,"done",Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .create().show();
            }
        });
        findViewById(R.id.lockBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 自定义对话框:LayoutInflater
                LayoutInflater layoutInflater = LayoutInflater.from(NoteActivity.this);
                View alertDialogLayout = layoutInflater.inflate(R.layout.layout_dialog,null);
                final TextView passwordET = (TextView) alertDialogLayout.findViewById(R.id.passwordET) ;
                final TextView repetitionET = (TextView) alertDialogLayout.findViewById(R.id.repetitionET) ;

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NoteActivity.this);
                alertDialog.setView(alertDialogLayout)
                        .setTitle("修改/添加密码")
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = passwordET.getText().toString() ;
                                String repetition = repetitionET.getText().toString() ;
                                if( !password.equals(repetition) )
                                    Toast.makeText(NoteActivity.this,"重复密码有误",Toast.LENGTH_SHORT).show();
                                else{
                                    pass = MD5Utils.md5Password(password) ;
                                }
                            }
                        })
                        .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .create().show();

            }
        });


    }

    // 选择相册或相机的dialog
    void setPictureMenuDialog(){
        pictureMenu = new Dialog(this,R.style.bottom_dialog) ;
        //填充对话框的布局
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_picture_menu, null);
        //将布局设置给Dialog
        pictureMenu.setContentView(linearLayout);
        //按键初始化
        cameraBtn = (Button) linearLayout.findViewById(R.id.cameraBtn);
        albumBtn = (Button) linearLayout.findViewById(R.id.albumBtn);
        //获取当前Activity所在的窗体
        Window dialogWindow = pictureMenu.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 设置Dialog动画
        dialogWindow.setWindowAnimations(R.style.dialog_style);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 窗体距离底部的距离
        lp.y = 150;  // 150为虚拟机虚拟键盘的高度
        // 窗体的高度、宽度和透明度
        lp.width = getResources().getDisplayMetrics().widthPixels-50; // 宽度
        linearLayout.measure(0, 0);
        lp.height = linearLayout.getMeasuredHeight();
//        lp.height = 450 ;
        lp.alpha = 9f;
        // 将属性设置给窗体
        dialogWindow.setAttributes(lp);
        pictureMenu.setCanceledOnTouchOutside(true);
        pictureMenu.show();
        PictureMenuRespond() ;
    }
    // 选择相册或相机dialog的响应
    void PictureMenuRespond(){
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pictureMenu.cancel();
                Intent intent = new Intent();
                // 调用系统的照相机
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "tmp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 3);
            }
        });
        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pictureMenu.cancel();
                Intent intent = new Intent() ;
                intent.setType("image/*") ;
                // 调用系统相册
                intent.setAction(Intent.ACTION_GET_CONTENT) ;
                startActivityForResult(intent,1);
            }
        });
    }
    // 裁剪图片——Intent跳转
    void cutImg(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            //true:出现裁剪的框
            intent.putExtra("crop", "true");
            //裁剪宽高时的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //裁剪后的图片的大小
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
//            intent.putExtra("return-data", true);  // 返回数据
//            intent.putExtra("output", uri);
//            intent.putExtra("scale", true);
//            startActivityForResult(intent, 2);

            /**
             * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
             * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
             */
            //intent.putExtra("return-data", true);

            //uritempFile为Uri类变量，实例化uritempFile
            uriTempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            startActivityForResult(intent, 2);
        } else {
            return;
        }
    }
    // 保存图片
    void saveImg(Bitmap bitmap) {
        try {
            File destDir = new File(fileBasePath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File f = new File(fileBasePath + System.currentTimeMillis()+ ".png");
            f.createNewFile();
            //输出流
            FileOutputStream out = new FileOutputStream(f);
            /** mBitmap.compress 压缩图片
             *
             *  Bitmap.CompressFormat.PNG   图片的格式
             *   100  图片的质量（0-100）
             *   out  文件输出流
             */
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            filePath = f.toString() ;
            System.out.println(filePath);
            richEditor.insertImage(filePath,"png");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置导航栏和状态栏的颜色
        StausBarColor.setWindowStatusBarColor(NoteActivity.this,R.color.gray);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        verifyPermission();

        init();

        getData();

        softKeyBoardListener();

        editToolResponse();

        noteToolResponse() ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode==RESULT_OK ){
            // 打开相册的回调函数
            if ( requestCode==1 ){
                if ( data!=null ){
                    Uri uri = data.getData();
                    cutImg(uri);
                }
            }
            // 打开系统裁剪界面的回调函数
            if (requestCode == 2) {
                if (data != null) {
                    //将Uri图片转换为Bitmap
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriTempFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    //得到图片
//                    Bundle bundle = data.getExtras();
//                    Bitmap bitmap = bundle.getParcelable("data");
                    //保存到图片到本地
                    saveImg(bitmap);
                } else {
                    return;
                }
            }
            // 打开相机的回调函数
            if ( requestCode == 3 ){

                cutImg(imageUri);

            }
        }
    }

    //重写返回键的操作
    @Override
    public void onBackPressed(){
        if ( id>0 ){
            String sql = "UPDATE Article set article='" + richEditor.getHtml() + "'" ;
            if ( !pass.equals("") ) sql += ", locked="+1+", password='"+pass+"'" ;
            sql += " WHERE id="+id ;
            db_helper.getWritableDatabase().execSQL(sql);
        }
        else{
            Calendar calendar=Calendar.getInstance();
            ContentValues values = new ContentValues() ;
            values.put("article",richEditor.getHtml());
            values.put("year",calendar.get(Calendar.YEAR));
            values.put("month",calendar.get(Calendar.MONTH)+1);
            values.put("day",calendar.get(Calendar.DAY_OF_MONTH));
            values.put("time",time);
            if ( pass.equals("") ) values.put("locked",0);
            else{
                values.put("locked",1);
                values.put("password",pass);
            }
            db_helper.getWritableDatabase().insert("Article",null,values) ;
        }
        finish() ;
    }

}
