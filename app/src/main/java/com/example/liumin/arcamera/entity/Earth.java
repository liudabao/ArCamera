package com.example.liumin.arcamera.entity;

import android.opengl.GLES20;


import com.example.liumin.arcamera.MatrixState;
import com.example.liumin.arcamera.ui.MySurfaceView;
import com.example.liumin.arcamera.util.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static com.example.liumin.arcamera.util.ShaderUtil.createProgram;


//��ʾ������࣬���ö�������
public class Earth  {	
	int mProgram;//�Զ�����Ⱦ���߳���id 
    int muMVPMatrixHandle;//�ܱ任��������id   
    int muMMatrixHandle;//λ�á���ת�任����
    int maCameraHandle; //�����λ����������id  
    int maPositionHandle; //����λ����������id  
    int maNormalHandle; //���㷨������������id  
    int maTexCoorHandle; //��������������������id  
    int maSunLightLocationHandle;//��Դλ����������id      
    int uDayTexHandle;//����������������id  
    int uNightTexHandle;//��ҹ������������id  
    String mVertexShader;//������ɫ��    	 
    String mFragmentShader;//ƬԪ��ɫ��
	FloatBuffer   mVertexBuffer;//�����������ݻ���
	FloatBuffer   mTexCoorBuffer;//���������������ݻ���
    int vCount=0; 
    public Earth(MySurfaceView mv, float r){
    	//��ʼ��������������ɫ����
    	initVertexData(r);
    	//��ʼ����ɫ��        
    	initShader(mv);
    } 
    //��ʼ�������������������ݵķ���
    public void initVertexData(float r){
    	//�����������ݵĳ�ʼ��================begin============================    	
    	final float UNIT_SIZE=0.5f;
    	ArrayList<Float> alVertix=new ArrayList<Float>();//��Ŷ��������ArrayList
    	final float angleSpan=10f;//������е�λ�зֵĽǶ�
    	for(float vAngle=90;vAngle>-90;vAngle=vAngle-angleSpan){//��ֱ����angleSpan��һ��
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan){//ˮƽ����angleSpan��һ��
        		//����������һ���ǶȺ�����Ӧ�Ĵ˵��������ϵ�����
        		double xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
        		float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y2=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));
        		xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
        		float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
        		float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
        		float y3=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));
        		xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
        		float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
        		float y4=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));   
        		//������һ������
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//�����ڶ�������
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
        }} 	
        vCount=alVertix.size()/3;//���������Ϊ����ֵ������1/3����Ϊһ��������3������
        //��alVertix�е�����ֵת�浽һ��float������
        float vertices[]=new float[vCount*3];
    	for(int i=0;i<alVertix.size();i++){
    		vertices[i]=alVertix.get(i);
    	}
        //���������������ݻ���
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //��alTexCoor�е���������ֵת�浽һ��float������
        float[] texCoor=generateTexCoor(//��ȡ�з���ͼ����������    
   			 (int)(360/angleSpan), //����ͼ�зֵ�����
   			 (int)(180/angleSpan)  //����ͼ�зֵ�����
        );
        ByteBuffer llbb = ByteBuffer.allocateDirect(texCoor.length*4);
        llbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer=llbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);    
    }
    public void initShader(MySurfaceView mv){ //��ʼ����ɫ��
    	//���ض�����ɫ���Ľű�����       
        mVertexShader= ShaderUtil.loadFromAssetsFile("vertex_earth.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");   
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_earth.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        ShaderUtil.checkGlError("==ss==");
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж���������������id   
        maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");  
        //��ȡ�����ж��㷨������������id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");   
        //��ȡ�����������λ������id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //��ȡ�����й�Դλ������id
        maSunLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocationSun");   
        //��ȡ���졢��ҹ������������
        uDayTexHandle=GLES20.glGetUniformLocation(mProgram, "sTextureDay");  
        uNightTexHandle=GLES20.glGetUniformLocation(mProgram, "sTextureNight");  
        //��ȡλ�á���ת�任��������id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
    }
    public void drawSelf(int texId,int texIdNight) {        
    	 //�ƶ�ʹ��ĳ����ɫ������
    	 GLES20.glUseProgram(mProgram);
         //�����ձ任��������ɫ������
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         //��λ�á���ת�任��������ɫ������
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);    
         //�������λ�ô�����ɫ������   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //����Դλ�ô�����ɫ������   
         GLES20.glUniform3fv(maSunLightLocationHandle, 1, MatrixState.lightPositionFBSun);
         GLES20.glVertexAttribPointer(//Ϊ����ָ������λ������    
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4, 
                mVertexBuffer   
         );       
         GLES20.glVertexAttribPointer(  //Ϊ����ָ��������������
        		maTexCoorHandle,  
         		2, 
         		GLES20.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         GLES20.glVertexAttribPointer    //Ϊ����ָ�����㷨��������  
         (
        		maNormalHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );            
         //������λ����������
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);           
         //������
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);    
         GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIdNight);            
         GLES20.glUniform1i(uDayTexHandle, 0);
         GLES20.glUniform1i(uNightTexHandle, 1);          
         //����������
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
    //�Զ��з����������������ķ���
    public float[] generateTexCoor(int bw,int bh){
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;//����
    	float sizeh=1.0f/bh;//����
    	int c=0;
    	for(int i=0;i<bh;i++){
    		for(int j=0;j<bw;j++){
    			//ÿ����һ�����Σ������������ι��ɣ��������㣬12����������
    			float s=j*sizew;
    			float t=i*sizeh;
    			result[c++]=s;
    			result[c++]=t;
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			result[c++]=s+sizew;
    			result[c++]=t;    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    	}}
    	return result;
}}
