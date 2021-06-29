# version 300 es

uniform mat4 uMVPMatrix;
in vec4 aPosition;
in vec4 aColor;       //顶点颜色
out  vec4 vColor;       //用于传递给片元着色器的变量


void main() { 
   gl_Position = uMVPMatrix * aPosition; 
   vColor = aColor;   //将接收的颜色传递给片元着色器
}