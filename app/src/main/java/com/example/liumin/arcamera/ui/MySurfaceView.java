package com.example.liumin.arcamera.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


public class MySurfaceView extends GLSurfaceView
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
   // private SceneRenderer mRenderer;//������Ⱦ��
	 

	
	//public MySurfaceView(Context context) {
   //     super(context);
   //     this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
       // mRenderer = new SceneRenderer();	//����������Ⱦ��
       // setRenderer(mRenderer);				//������Ⱦ��
       // setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ
   // }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2);
    }


    public void onResume(){
        Log.e("Camera", "resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        Log.e("Camera", "pause");
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
    }

}
