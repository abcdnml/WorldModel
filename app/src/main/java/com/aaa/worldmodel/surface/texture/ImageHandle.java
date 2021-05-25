package com.aaa.worldmodel.surface.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.aaa.worldmodel.utils.LogUtils;
import com.aaa.worldmodel.surface.GLDrawable;
import com.aaa.worldmodel.twodimensional.ShaderUtil;

import java.nio.FloatBuffer;

public class ImageHandle extends GLDrawable {


    private static final int LOCATION_VERTEX_COORDINATE = 0;
    private static final int LOCATION_TEXTURE_COORDINATE = 1;
    private static final int LOCATION_MATRIX = 2;
    private static final int LOCATION_TEXTURE = 3;
    private static final int LOCATION_EFFECT_TYPE = 4;
    private static final int LOCATION_EFFECT_COLOR = 5;
    private static final int LOCATION_IS_HALF = 6;
    private static final int LOCATION_XY = 7;
    private static final int VERTEX_SIZE = 2;
    private static final int COLOR_SIZE = 2;
    protected static int programId;
    static String vertexShaderCode;
    static String fragmentShaderCode;
    int effectType;
    int isHalf;
    float[] effectColor;
    float uXY;
    private Bitmap mBitmap;
    private float[] mMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    public ImageHandle(Context context, Bitmap bitmap, FloatBuffer glCoordinate, FloatBuffer textureCoordinate, int effectType, float[] effectColor, boolean isHalf) {
        super(context);
        mBitmap = bitmap;
        vertexBuffer = glCoordinate;
        colorBuffer = textureCoordinate;
        this.effectType = effectType;
        this.effectColor = effectColor;
        this.isHalf = isHalf ? 1 : 0;

    }

    @Override
    public void setModelMatrix(float[] matrix) {

    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/image_effect.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/image_effect.frag", context.getResources());
        programId = createGLProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            LogUtils.e("Program id is 0 ,may not init");
            return;
        }
        LogUtils.i("onDrawFrame texture programId: " + programId);
        GLES30.glUseProgram(programId);

        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX_COORDINATE);
        GLES30.glEnableVertexAttribArray(LOCATION_TEXTURE_COORDINATE);

        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);
        GLES30.glUniform1i(LOCATION_TEXTURE, 0);
        GLES30.glUniform1i(LOCATION_EFFECT_TYPE, effectType);
        GLES30.glUniform1i(LOCATION_IS_HALF, isHalf);
        GLES20.glUniform3fv(LOCATION_EFFECT_COLOR, 1, effectColor, 0);
        GLES20.glUniform1f(LOCATION_XY, uXY);

        int textureId = createTexture();

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//激活纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);//绑定纹理

        GLES30.glVertexAttribPointer(LOCATION_VERTEX_COORDINATE, VERTEX_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, vertexBuffer);
        GLES30.glVertexAttribPointer(LOCATION_TEXTURE_COORDINATE, COLOR_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, colorBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX_COORDINATE);
        GLES30.glDisableVertexAttribArray(LOCATION_TEXTURE_COORDINATE);
    }

    private int createTexture() {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //生成纹理ID
            GLES30.glGenTextures(1, texture, 0);
            LogUtils.e("glGenTextures : " + texture[0]);
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

    @Override
    public void onSurfaceChange(int width, int height) {
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        uXY=sWH;
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
    }
}
