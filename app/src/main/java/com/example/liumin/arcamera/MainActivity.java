package com.example.liumin.arcamera;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.liumin.arcamera.ui.CameraRender;
import com.example.liumin.arcamera.ui.MySurfaceView;
import com.example.liumin.arcamera.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends Activity implements View.OnTouchListener {

    private MySurfaceView mGLSurfaceView;
    private CircleImageView previewImage;
    private ImageButton capture;
    private ImageButton arBtn;
    private TextView openText;
    private TextView closeText;

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    CameraRender cameraRender;
    private List<String> list=new ArrayList<String>();

    private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����
    private float distantX;
    private float distantY;
    int width;
    int height;
    /**
     * ��¼������ģʽ���ǷŴ���Сģʽ
     */
    private int mode = 0;// ��ʼ״̬

    /**
     * ���ڼ�¼��ʼʱ�������λ��
     */
    private PointF startPoint = new PointF();
    /**
     * ������ָ�Ŀ�ʼ����
     */
    private float startDis;
    /**
     * ������ָ���м��
     */
    private PointF midPoint;

    private String path;
    private IntentFilter intentFilter;
    private PictureBroad pictureBroad;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private boolean isAr = true;
    private boolean isSelect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //����Ϊ����ģʽ
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getPersimmions();
        init();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void init() {
        //��ʼ��GLSurfaceView
        arBtn = (ImageButton)findViewById(R.id.btn_ar);
        openText = (TextView)findViewById(R.id.open);
        closeText = (TextView) findViewById(R.id.close);
        mGLSurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);
        previewImage = (CircleImageView) findViewById(R.id.image_preview);
        capture = (ImageButton) findViewById(R.id.capture);
        //SceneRenderer sceneRenderer=new SceneRenderer(mGLSurfaceView);
        cameraRender = new CameraRender(mGLSurfaceView, this);
        // EarthRender earthRender=new EarthRender(mGLSurfaceView, this);
        // mGLSurfaceView.setRenderer(sceneRenderer);
        mGLSurfaceView.setRenderer(cameraRender);
        //  mGLSurfaceView.setRenderer(earthRender);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //�л���������
        mGLSurfaceView.requestFocus();//��ȡ����
        mGLSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���
        mGLSurfaceView.setOnTouchListener(this);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraRender.setCapture(true);
                list.add(cameraRender.getFileName());
                showImage();
            }
        });

        previewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    //intent.putExtra("PATH", path);
                    startActivityForResult(intent, 0);

                }

            }
        });

        arBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSelect){
                    animatorOpen();
                    isSelect =false;
                }
                else {
                    animatorClose();
                    isSelect =true;
                }
            }
        });

        openText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isAr){
                    arBtn.setBackgroundResource(R.drawable.bg_btn_ar_select);
                    openText.setBackgroundResource(R.drawable.bg_textview_open);
                    closeText.setBackgroundResource(R.drawable.bg_textview_close);
                    isAr=true;
                    cameraRender.setIsAr(isAr);
                }
                animatorClose();
                isSelect=true;
            }
        });

        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAr){
                    arBtn.setBackgroundResource(R.drawable.bg_btn_ar);
                    openText.setBackgroundResource(R.drawable.bg_textview_close);
                    closeText.setBackgroundResource(R.drawable.bg_textview_open);
                    isAr=false;
                    cameraRender.setIsAr(isAr);
                }
                animatorClose();
                isSelect=true;
            }
        });

        showImage();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.liu.ar");
        pictureBroad = new PictureBroad();
        registerReceiver(pictureBroad, intentFilter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
                    showImage();
                }
        }
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
            previewImage.setImageBitmap(bitmap);
        } else {
            //previewImage.setBackground(R.color.gray);
            //previewImage.setBackgroundColor(R.color.gray);
            previewImage.setBackgroundResource(R.drawable.bg_imageview_preview);
        }
    }

    private void animatorOpen(){
        openText.setVisibility(View.VISIBLE);
        closeText.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(openText, "translationX",arBtn.getWidth(), 160f);
        ObjectAnimator objectAnimator1 = new ObjectAnimator().ofFloat(openText,"alpha", 0f, 1f);
        ObjectAnimator objectAnimator2 = new ObjectAnimator().ofFloat(closeText, "translationX",arBtn.getWidth(), 320f);
        ObjectAnimator objectAnimator3 = new ObjectAnimator().ofFloat(closeText,"alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, objectAnimator1, objectAnimator2, objectAnimator3);
       // animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setDuration(500).start();

    }

    private void animatorClose(){
        ObjectAnimator objectAnimator =new ObjectAnimator().ofFloat(openText, "translationX",  160f, 0);
        ObjectAnimator objectAnimator1 = new ObjectAnimator().ofFloat(openText,"alpha", 1f, 0f);
        ObjectAnimator objectAnimator2 = new ObjectAnimator().ofFloat(closeText, "translationX",  320f, 0);
        ObjectAnimator objectAnimator3 = new ObjectAnimator().ofFloat(closeText,"alpha", 1f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, objectAnimator1, objectAnimator2, objectAnimator3);
       // animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.setDuration(500).start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                openText.setVisibility(View.GONE);
                closeText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
    //�����¼��ص�����

    public boolean onTouch(View v, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.e("Scale", "------down-------");
                mode = 1;
                mPreviousX = x;//��¼���ر�λ��
                mPreviousY = y;
                startPoint.set(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                Log.e("Scale", "------move-------");
                if (mode == 1) {
                    //���غ���λ��̫����y����ת
                    distantX = (x - mPreviousX) * 2 / width;
                    distantY = (y - mPreviousY) * 2 / height;
                    Log.e("Transtate", distantX + " " + distantY);
                    cameraRender.setTranstateX(distantX);
                    cameraRender.setTranstateY(-distantY);
                }
                // �Ŵ���С
                else if (mode >= 2) {
                    float endDis = (float) distance(e);// ��������
                    if (endDis > 10f) { // ������ָ��£��һ���ʱ�����ش���10
                        float scale = endDis / startDis;// �õ����ű���
                        cameraRender.setScale(scale);
                        Log.e("Scale", "------" + scale + "-------");
                    }
                }
                mPreviousX = x;//��¼���ر�λ��
                mPreviousY = y;
                break;

            case MotionEvent.ACTION_UP:
                Log.e("Scale", "------up-------");
                mode = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e("Scale", "------up 1-------");
                mode -= 1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode += 1;
                Log.e("Scale", "-----down 2------");
                /** ����������ָ��ľ��� */
                startDis = (float) distance(e);
                /** ����������ָ����м�� */
                if (startDis > 10f) { // ������ָ��£��һ���ʱ�����ش���10
                    midPoint = mid(e);
                }
                //��¼��ǰImageView�����ű���}
                break;
        }

        return true;
    }

    /**
     * ����������ָ��ľ���
     */
    private double distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** ʹ�ù��ɶ���������֮��ľ��� */
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * ����������ָ����м��
     */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraRender.setThreadFlag(true);
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraRender.setThreadFlag(false);
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pictureBroad);
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();

            // ��дȨ��
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            } else if (addPermission(permissions, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                permissionInfo += "Manifest.permission.SYSTEM_ALERT_WINDOW Deny \n";
            } else if (addPermission(permissions, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
                permissionInfo += "Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS Deny \n";
            } else if (addPermission(permissions, Manifest.permission.CAMERA)) {
                permissionInfo += "Manifest.permission.CAMERA Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }

    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // ���Ӧ��û�л�ö�ӦȨ��,����ӵ��б���,׼����������
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SDK_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("permission", "success");
                } else {
                    Log.e("permission", "fail");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    class PictureBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG", "receive picture");
            showImage();
        }
    }
}
