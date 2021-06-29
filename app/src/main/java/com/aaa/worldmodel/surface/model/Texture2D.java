package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.twodimensional.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Texture2D extends Model {
    private static final int LOCATION_VERTEX = 0;
    private static final int LOCATION_COLOR = 1;
    private static final int LOCATION_MATRIX = 2;
    private static final int LOCATION_TEXTURE = 3;
    private static final String TAG = Texture2D.class.getSimpleName();

    private final float[] mMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];

    private static final int VERTEX_SIZE = 2;
    private static final int COLOR_SIZE = 2;

    protected Bitmap mBitmap;

    public Texture2D(Context context,Bitmap bitmap, float[] vertex, float[] textureCoordinate) {
        super(context);
        vertexShaderCode = "# version 300 es \n" +
                "layout (location = 0) in vec4 vPosition;\n" +
                "layout (location = 1) in vec2 vCoordinate;\n" +
                "layout (location = 2) uniform mat4 u_Matrix;\n" +
                "out vec2 aCoordinate;\n" +
                "void main(){" +
                "    gl_Position=u_Matrix*vPosition;" +
                "    aCoordinate=vCoordinate;" +
                "}";
        fragmentShaderCode = "# version 300 es \n" +
                "precision mediump float;" +
                "layout (location = 3) uniform sampler2D vTexture;" +
                "in vec2 aCoordinate;" +
                "out vec4 fragColor;" +
                "void main(){" +
                "    fragColor=texture(vTexture,aCoordinate);" +
                "}";

        setVertex(vertex);
        setVertexColor(textureCoordinate);
        this.mBitmap = bitmap;
    }

    public void setVertex(float[] vertex) {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertex);
        vertexBuffer.flip();
    }

    public void setVertexColor(float[] color) {
        colorBuffer = ByteBuffer.allocateDirect(color.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(color);
        colorBuffer.flip();
    }


    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix,float[] pMatrix) {

    }

    @Override
    public void onSurfaceCreate(Context context) {
        programId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            Log.e(TAG, "Program id is 0 ,may not init");
            return;
        }
        Log.i(TAG, "onDrawFrame texture programId: " + programId);
        GLES30.glUseProgram(programId);

        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);

        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);

        GLES30.glUniform1i(LOCATION_TEXTURE, 0);
        int textureId = createTexture();
        //激活纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);


        GLES30.glVertexAttribPointer(LOCATION_VERTEX, VERTEX_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, vertexBuffer);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, COLOR_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, colorBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glDisableVertexAttribArray(LOCATION_COLOR);

    }

    @Override
    public void onSurfaceChange(int width, int height) {
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        Log.e(TAG, "onSurfaceChange w: " + w+" h: "+h);
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
         Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMatrix, 0, mProjMatrix, 0, mVMatrix, 0);


//        Matrix.frustumM(mProjMatrix, 0, -sWidthHeight, sWidthHeight, -1, 1, 1, 10);//调用此方法计算产生透视投影矩阵

//        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f); //调用此方法产生摄像机9参数位置矩阵
//        Matrix.multiplyMM(mMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理ID
            GLES30.glGenTextures(1, texture, 0);
            Log.e(TAG, "glGenTextures : " + texture[0]);

            //绑定纹理ID
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]);

            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);


            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, mBitmap, 0);
            // 生成MIP贴图
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

            // 数据如果已经被加载进OpenGL,则可以回收该bitmap
//            bitmap.recycle();

            // 取消绑定纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
            return texture[0];
        }
        return 0;
    }
}
