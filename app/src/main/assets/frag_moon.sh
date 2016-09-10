//������ɫ��
precision mediump float;
varying vec2 vTextureCoord;//���մӶ�����ɫ�������Ĳ���
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
uniform sampler2D sTexture;//������������
void main()                         
{  
  //����ƬԪ�������в�������ɫֵ            
  vec4 finalColor = texture2D(sTexture, vTextureCoord); 
  //����ƬԪ��ɫֵ 
  gl_FragColor = finalColor*vAmbient+finalColor*vSpecular+finalColor*vDiffuse;
}              