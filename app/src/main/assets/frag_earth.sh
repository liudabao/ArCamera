//������ɫ��
precision mediump float;
varying vec2 vTextureCoord;//���մӶ�����ɫ�������Ĳ���
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
uniform sampler2D sTextureDay;//������������
uniform sampler2D sTextureNight;//������������
void main()                         
{  
  //����ƬԪ�������в�������ɫֵ   
  vec4 finalColorDay;   
  vec4 finalColorNight;   
  
  finalColorDay= texture2D(sTextureDay, vTextureCoord);
  finalColorDay = finalColorDay*vAmbient+finalColorDay*vSpecular+finalColorDay*vDiffuse;
  finalColorNight = texture2D(sTextureNight, vTextureCoord); 
  finalColorNight = finalColorNight*vec4(0.5,0.5,0.5,1.0);
  
  if(vDiffuse.x>0.21)
  {
    gl_FragColor=finalColorDay;    
  } 
  else if(vDiffuse.x<0.05)
  {     
     gl_FragColor=finalColorNight;
  }
  else
  {
     float t=(vDiffuse.x-0.05)/0.16;
     gl_FragColor=t*finalColorDay+(1.0-t)*finalColorNight;
  }  
}              