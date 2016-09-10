package com.example.liumin.arcamera.entity;

import android.opengl.GLES20;


import com.example.liumin.arcamera.util.ShaderUtil;
import com.example.liumin.arcamera.MatrixState;
import com.example.liumin.arcamera.ui.MySurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class Celestial {	//��ʾ�ǿ��������
	final float UNIT_SIZE=10.0f;//����뾶
	private FloatBuffer   mVertexBuffer;//�����������ݻ���
    int vCount=0;//��������
    float yAngle;//������Y����ת�ĽǶ�
    float scale;//���ǳߴ�  
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
    int mProgram;//�Զ�����Ⱦ���߳���id 
    int muMVPMatrixHandle;//�ܱ任��������id   
    int maPositionHandle; //����λ����������id  
    int uPointSizeHandle;//����ߴ��������
    public Celestial(float scale,float yAngle,int vCount,MySurfaceView mv){
    	this.yAngle=yAngle;
    	this.scale=scale;
    	this.vCount=vCount;  
    	initVertexData();
    	intShader(mv);
    }
    public void initVertexData(){ //��ʼ����������ķ���    	  	
    	//�����������ݵĳ�ʼ��       
        float vertices[]=new float[vCount*3];
        for(int i=0;i<vCount;i++){
        	//�������ÿ�����ǵ�xyz����
        	double angleTempJD=Math.PI*2*Math.random();
        	double angleTempWD=Math.PI*(Math.random()-0.5f);
        	vertices[i*3]=(float)(UNIT_SIZE*Math.cos(angleTempWD)*Math.sin(angleTempJD));
        	vertices[i*3+1]=(float)(UNIT_SIZE*Math.sin(angleTempWD));
        	vertices[i*3+2]=(float)(UNIT_SIZE*Math.cos(angleTempWD)*Math.cos(angleTempJD));
        }
        //���������������ݻ���
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
    }
    public void intShader(MySurfaceView mv){    //��ʼ����ɫ��
    	//���ض�����ɫ���Ľű�����       
        mVertexShader= ShaderUtil.loadFromAssetsFile("vertex_xk.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");   
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_xk.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        ShaderUtil.checkGlError("==ss==");      
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");        
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //��ȡ����ߴ��������
        uPointSizeHandle = GLES20.glGetUniformLocation(mProgram, "uPointSize"); 
    }
    public void drawSelf(){  
   	    GLES20.glUseProgram(mProgram); //�ƶ�ʹ��ĳ����ɫ������
        //�����ձ任��������ɫ������
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(uPointSizeHandle, scale);  //������ߴ紫����ɫ������
        GLES20.glVertexAttribPointer( //Ϊ����ָ������λ������    
        		maPositionHandle,   
        		3, 
        		GLES20.GL_FLOAT, 
        		false,
                3*4, 
                mVertexBuffer   
        );   
        //������λ����������
        GLES20.glEnableVertexAttribArray(maPositionHandle);         
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount); //�������ǵ�    
}}
