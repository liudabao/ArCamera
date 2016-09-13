package com.example.liumin.arcamera.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;


import com.example.liumin.arcamera.R;
import com.example.liumin.arcamera.entity.Earth;
import com.example.liumin.arcamera.MatrixState;
import com.example.liumin.arcamera.entity.DirectDrawer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liumin on 2016/8/24.
 */
public class CameraRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "LIU";
    //Context mContext;
    SurfaceTexture mSurface;
    int mTextureID = -1;
    DirectDrawer mDirectDrawer;
    MySurfaceView glSurfaceView;
    Context mContext;

    int textureIdEarth;//ϵͳ����ĵ�������id
    int textureIdEarthNight;//ϵͳ����ĵ���ҹ������id
    float eAngle=0;//������ת�Ƕ�
    Earth earth;//����
    float ratio;
    private boolean threadFlag=true;
    private float transtateX=0;
    private float transtateY=0;
    private float currentX=0;
    private float currentY=0;
    private float scale=1;

    private boolean mCapture=false;
    private int mViewWidth = 0;
    private int mViewHeight = 0;

    private String fileName;
    private boolean isAr=true;

    public CameraRender(MySurfaceView mySurfaceView, Context context){
        glSurfaceView=mySurfaceView;
        mContext = context;
    }

    public void setTranstateX(float transtateX) {
        this.transtateX = transtateX;
        currentX = currentX + transtateX;
    }

    public void setTranstateY(float transtateY) {
        this.transtateY = transtateY;
        currentY = currentY + transtateY;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setThreadFlag(boolean flag) {
        this.threadFlag = flag;
    }

    public void setIsAr(boolean isAr) {
        this.isAr = isAr;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated...");
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mTextureID);
        CameraInterface.getInstance().doOpenCamera(null);

        //�����������
        earth=new Earth(glSurfaceView,2.0f);
        //����ȼ��
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //��ʼ���任����
        MatrixState.setInitStack();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged...");
        GLES20.glViewport(0, 0, width, height);
        if (!CameraInterface.getInstance().isPreviewing()) {
            CameraInterface.getInstance().doStartPreview(mSurface, 1.33f);
        }

        //����GLSurfaceView�Ŀ�߱�
        ratio= (float) width / height;
        //���ô˷����������͸��ͶӰ����
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
        //���ô˷������������9����λ�þ���
        MatrixState.setCamera(0, 0, 7.2f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //�򿪱������
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //��ʼ������
        textureIdEarth=initTexture(R.drawable.earth);
        textureIdEarthNight=initTexture(R.drawable.earthn);
        //����̫���ƹ�ĳ�ʼλ��
        MatrixState.setLightLocationSun(100, 5, 0);

        //����һ���̶߳�ʱ��ת��������
        new Thread()
        {
            public void run()
            {
                while(threadFlag)
                {
                    //������ת�Ƕ�
                    eAngle=(eAngle+2)%360;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //
        mViewWidth = width;
        mViewHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
       // Log.i(TAG, "onDrawFrame...");
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);

        if(isAr){
            //�����ֳ�
            MatrixState.pushMatrix();
            //������ת
            MatrixState.scale(0.5f*scale, 0.5f*scale, 0.5f*scale);
            MatrixState.transtate(currentX, currentY, 0);
            MatrixState.rotate(eAngle, 0, 1, 0);
            //��������Բ��
            earth.drawSelf(textureIdEarth, textureIdEarthNight);
            //�ָ��ֳ�
            MatrixState.popMatrix();
        }

        //
        mDirectDrawer.draw(mtx);
        if(mCapture){
            Log.e("Ar Tag", "start..............................................");
            saveScreenShot(0, 0, mViewWidth, mViewHeight);
            Log.e("Ar Tag", "end..............................................");
            mCapture=false;
        }
    }

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
       // Log.i(TAG, "onFrameAvailable...");
        glSurfaceView.requestRender();
    }

    public int initTexture(int drawableId)//textureId
    {
        //��������ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //����������id������
                        textures,   //����id������
                        0           //ƫ����
                );
        int textureId=textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //ͨ������������ͼƬ===============begin===================
        InputStream is = mContext.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try
        {
            bitmapTmp = BitmapFactory.decodeStream(is);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        //ͨ������������ͼƬ===============end=====================
        //ʵ�ʼ�������
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
                        0,                      //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
                        bitmapTmp,              //����ͼ��
                        0                      //����߿�ߴ�
                );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ

        return textureId;
    }

    private void saveScreenShot(int x, int y, int w, int h) {
        Bitmap bmp = grabPixels(x, y, w, h);
        try {
            fileName = Environment.getExternalStorageDirectory() + "/" + getRandomFileName()+".png";
            //DebugLog.LOGD(path);
            Log.e("TAG", fileName);
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            Log.e("TAG ERROR", e.getStackTrace().toString());
        }finally {
            Intent intent =new Intent("com.liu.ar");
            mContext.sendBroadcast(intent);
        }

    }

    private Bitmap grabPixels(int x, int y, int w, int h) {
        int b[] = new int[w * (y + h)];
        int bt[] = new int[w * h];
        IntBuffer ib = IntBuffer.wrap(b);
        ib.position(0);

        GLES20.glReadPixels(x, 0, w, y + h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);

        for (int i = 0, k = 0; i < h; i++, k++) {
            for (int j = 0; j < w; j++) {
                int pix = b[i * w + j];
                int pb = (pix >> 16) & 0xff;
                int pr = (pix << 16) & 0x00ff0000;
                int pix1 = (pix & 0xff00ff00) | pr | pb;
                bt[(h - k - 1) * w + j] = pix1;
            }
        }

        Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;
    }

    public  String getRandomFileName() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

        return str ;// 当前时间
    }

    public void setCapture(boolean mCapture){
        this.mCapture=mCapture;
    }

}
