//�ǿ���ɫ��
uniform mat4 uMVPMatrix; //�ܱ任����
uniform float uPointSize;//��ߴ�
attribute vec3 aPosition;  //����λ��

void main()     
{     
   //�����ܱ任�������˴λ��ƴ˶���λ��                         		
   gl_Position = uMVPMatrix * vec4(aPosition,1); 
   //
   gl_PointSize=uPointSize;
}                 