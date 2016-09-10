package com.example.liumin.arcamera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;


import com.example.liumin.arcamera.MatrixState;
import com.example.liumin.arcamera.R;
import com.example.liumin.arcamera.entity.Celestial;
import com.example.liumin.arcamera.entity.Earth;
import com.example.liumin.arcamera.entity.Moon;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liumin on 2016/9/8.
 */
public class EarthRender implements GLSurfaceView.Renderer {

    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���

    private float mPreviousX;//�ϴεĴ���λ��X����
    private float mPreviousY;//�ϴεĴ���λ��Y����

    int textureIdEarth;//ϵͳ����ĵ�������id
    int textureIdEarthNight;//ϵͳ����ĵ���ҹ������id
    int textureIdMoon;//ϵͳ�������������id

    float yAngle=0;//̫���ƹ���y����ת�ĽǶ�
    float xAngle=0;//�������X����ת�ĽǶ�

    float eAngle=0;//������ת�Ƕ�
    float cAngle=0;//������ת�ĽǶ�

    Earth earth;//����
    Moon moon;//����
    Celestial cSmall;//С��������
    Celestial cBig;//����������

    float ratio;
    public static boolean threadFlag=true;

    Context mContext;
    MySurfaceView mySurfaceView;


    public EarthRender(MySurfaceView mySurfaceView, Context context) {
        mContext=context;
        this.mySurfaceView= mySurfaceView;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //������Ļ����ɫRGBA
        GLES20.glClearColor(0.0f,0.0f,0.0f, 1.0f);
        //�����������
        earth=new Earth(mySurfaceView,2.0f);
        //�����������
        moon=new Moon(mySurfaceView,1.0f);
        //����С�����������
        cSmall=new Celestial(1,0,1000,mySurfaceView);
        //�����������������
        cBig=new Celestial(2,0,500,mySurfaceView);
        //����ȼ��
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //��ʼ���任����
        MatrixState.setInitStack();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //�����Ӵ���С��λ��
        GLES20.glViewport(0, 0, width, height);
        //����GLSurfaceView�Ŀ�߱�
        ratio= (float) width / height;
        //���ô˷����������͸��ͶӰ����
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
        //���ô˷������������9����λ�þ���
        MatrixState.setCamera(0,0,7.2f,0f,0f,0f,0f,1.0f,0.0f);
        //�򿪱������
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //��ʼ������
        textureIdEarth=initTexture(R.drawable.earth);
        textureIdEarthNight=initTexture(R.drawable.earthn);
        textureIdMoon=initTexture(R.drawable.moon);
        //����̫���ƹ�ĳ�ʼλ��
        MatrixState.setLightLocationSun(100,5,0);

        //����һ���̶߳�ʱ��ת��������
        new Thread()
        {
            public void run()
            {
                while(threadFlag)
                {
                    //������ת�Ƕ�
                    eAngle=(eAngle+2)%360;
                    //������ת�Ƕ�
                    cAngle=(cAngle+0.2f)%360;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();


    }

    @Override
    public void onDrawFrame(GL10 gl) {
       //�����Ȼ�������ɫ����
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        //�����ֳ�
        MatrixState.pushMatrix();
        //������ת
        MatrixState.rotate(eAngle, 0, 1, 0);
        //��������Բ��
        earth.drawSelf(textureIdEarth,textureIdEarthNight);
        //������ϵ������λ��
      //  MatrixState.transtate(2f, 0, 0);
        //������ת
      //  MatrixState.rotate(eAngle, 0, 1, 0);
        //��������
       // moon.drawSelf(textureIdMoon);
        //�ָ��ֳ�
        MatrixState.popMatrix();

        //�����ֳ�
      //  MatrixState.pushMatrix();
       // MatrixState.rotate(cAngle, 0, 1, 0);
       // cSmall.drawSelf();
       // cBig.drawSelf();
        //�ָ��ֳ�
       // MatrixState.popMatrix();
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
}
