package com.example.liumin.arcamera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.liumin.arcamera.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class EditActivity extends Activity {

    private ImageButton back;
    private ImageView imageView;
    private ImageButton share;
    private ImageButton delete;
    private RelativeLayout top;
    private RelativeLayout bottom;
    private LinearLayout edit_layout;
    private TextView imageName;
    //private RecyclerView recyclerView;
    //private LinearLayoutManager linearLayoutManager;
    private String path;
    private List<String> list;
   // private MyAdapter mAdapter;

    private float topHeight;
    private float bottomHeight;
    private boolean flag=true;
    private boolean isDelete=true;

    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
       //         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit);
       // path= getIntent().getStringExtra("PATH");
        //initData();
        initView();
        initEvent();
    }

    private void initData(){
        list=new ArrayList();
        list.add("1");
        list.add("2");
    }

    private void initView(){
        back=(ImageButton) findViewById(R.id.back);
        share=(ImageButton) findViewById(R.id.bottom_btn_share);
        delete=(ImageButton) findViewById(R.id.bottom_btn_delete);
        imageView=(ImageView) findViewById(R.id.picture);
        top=(RelativeLayout)findViewById(R.id.top);
        bottom=(RelativeLayout)findViewById(R.id.bottom);
        edit_layout=(LinearLayout)findViewById(R.id.edit_layout);
        imageName=(TextView)findViewById(R.id.imageName);
       // recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
       // linearLayoutManager=new LinearLayoutManager(this);
       // linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
       // mAdapter = new MyAdapter(this, list);
        showImage();
       // if(path!=null){
       //     Bitmap bitmap= BitmapFactory.decodeFile(path);
       //     imageView.setImageBitmap(bitmap);
       // }
       // recyclerView.setLayoutManager(linearLayoutManager);
      //  recyclerView.setAdapter(mAdapter);
        float density = getResources().getDisplayMetrics().density;
        topHeight=(float)(top.getHeight()*density);
        bottomHeight=(float)(bottom.getHeight()*density);

        gestureDetector = new GestureDetector(this, onGestureListener);
        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("event", "double click");
                if(isDelete){
                    edit_layout.removeView(top);
                    edit_layout.removeView(bottom);
                    isDelete=false;
                }
                else {
                    edit_layout.addView(top);
                    edit_layout.addView(bottom);
                }
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        /*switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.e("event", "down");
                if(flag){
                    animateMove(top, -top.getHeight());
                    animateMove(bottom, bottom.getHeight());
                    flag=false;
                }
                else {
                    animateMove(top, topHeight);
                    animateMove(bottom, bottomHeight);
                    flag=true;
                }
                break;
            default:
                break;
        }*/
        return gestureDetector.onTouchEvent(event);
       // return super.onTouchEvent(event);
    }

    private void initEvent(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_OK);
                finish();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
                Toast.makeText(EditActivity.this, "test", Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("TAG", "delete click");
                showDialog();
               // Toast.makeText(GlobalContext.getContext(), "test", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDialog(){

        new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setMessage("确定删除本照片")
        .setCancelable(true)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                File file =new File(path);
                if(file.exists()){
                    file.delete();
                }
                showImage();
            }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
        .create().show();

    }

    private void showShare() {

        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }

    private void showImage() {
        list= FileUtil.getFile(Environment.getExternalStorageDirectory());
        //path = cameraRender.getFileName();
        if(list.size()>0){
            path= list.get(list.size()-1);
        }
        Log.e("TAG", path + "");
        if (path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
            File file=new File(path);
            imageName.setText(file.getName());
        } else {
            //previewImage.setBackground(R.color.gray);
            //previewImage.setBackgroundColor(R.color.gray);
        }
    }

    private void animateMove(View view, float height){
        //view.setVisibility(View.VISIBLE);
        Log.e("animator", "move: "+height);
        new ObjectAnimator().ofFloat(view, "translationY", height)
                .setDuration(500)
                .start();
    }

   // private void animateDown(View view, float height){
   //     Log.e("animator", "down");
   //     new ObjectAnimator().ofFloat(view, "translationY", height)
   //             .setDuration(1000)
   //             .start();
   // }


    private void animateOpen(View view, int height){
        view.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = createAnimator(view, 0 ,height);
       // valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    private void animateClose(final  View view){
        int height = view.getHeight();
        ValueAnimator valueAnimator = createAnimator(view, height, 0);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
       // valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    private ValueAnimator createAnimator(final  View view, int start, int end){
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                view.setLayoutParams(layoutParams);
            }
        });
        return valueAnimator;
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {

            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e("event", "down");
            if(flag){
                animateMove(top, -top.getHeight());
                animateMove(bottom, bottom.getHeight());
                flag=false;
            }
            else {
                animateMove(top, topHeight);
                animateMove(bottom, bottomHeight);
                flag=true;
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };
}
