package com.aaa.worldmodel.twodimensional;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.aaa.worldmodel.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Description
 * Created by aaa on 2017/11/16.
 */

public class Triangle
{
    public static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    public static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    public static float[] mMVPMatrix;//最后起作用的总变换矩阵

    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id
    int maColorHandle; //顶点颜色属性引用id
    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器
    static float[] mMMatrix = new float[16];//具体物体的移动旋转矩阵，旋转、平移

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer   mColorBuffer;//顶点着色数据缓冲
    int vCount=0;
    float xAngle=0;//绕x轴旋转的角度
    public Triangle(TwoDimensionalView mv)
    {
        //初始化顶点坐标与着色数据
        initVertexData();
        //初始化shader
        initShader(mv);
    }

    public void initVertexData()
    {
        //顶点坐标数据的初始化
        vCount=6;
        final float UNIT_SIZE=0.2f;
        //  final float UNIT_SIZE=0.1f;

        float vertices[]=new float[]
        {
                -4*UNIT_SIZE,0,0,
                0,-4*UNIT_SIZE,0,
                4*UNIT_SIZE,0,0,
                0,4*UNIT_SIZE,0,
                -4*UNIT_SIZE,0,0,
                4*UNIT_SIZE,0,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float colors[]=new float[]
                {
                        1,1,1,0,
                        0,0,1,0,
                        0,1,0,0,
                        0,1,1,0,
                        1,1,0,0,
                        1,0,0,0
                };

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    //初始化shader
    public void initShader(TwoDimensionalView mv)
    {
        mVertexShader= ShaderUtil.loadFromAssetsFile("triangle.vert", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("triangle.frag", mv.getResources());

        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf()
    {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //初始化变换矩阵
        Matrix.setRotateM(mMMatrix,0,0,0,1,0);
        //设置沿Z轴正向位移1
        Matrix.translateM(mMMatrix,0,0,0,1);
        //设置绕x轴旋转
        Matrix.rotateM(mMMatrix,0,xAngle,0,0,1);
        //
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle,1, false, Triangle.getFianlMatrix(mMMatrix), 0);
        //为画笔指定顶点位置数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maPositionHandle,3,GLES20.GL_FLOAT,false,3*4,mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        GLES20.glVertexAttribPointer(maColorHandle,4, GLES20.GL_FLOAT,false,4*4,mColorBuffer);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
    public static float[] getFianlMatrix(float[] spec)
    {
        mMVPMatrix=new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}