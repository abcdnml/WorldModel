package com.aaa.worldmodel.surface.obj;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.aaa.worldmodel.surface.GLDrawable;
import com.aaa.worldmodel.twodimensional.ShaderUtil;
import com.aaa.worldmodel.utils.LogUtils;

import java.io.IOException;

public class Obj3DShape extends GLDrawable {
    private static final int LOCATION_POSITION = 0;
    private static final int LOCATION_COORDINATE = 1;
    private static final int LOCATION_NORMAL = 2;
    private static final int LOCATION_MATRIX = 3;
    private static final int LOCATION_TEXTURE = 3;
    static int programId;
    static String vertexShaderCode;
    static String fragmentShaderCode;
    public boolean temp;
    Obj3D obj;
    int r = 3;
    float angle = 0;
    float aspectRatio = 1;
    float scale = 0.5f;
    private float[] mMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] offset = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
    private float[] offsetTemp = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
    private float[] mVMatrix = new float[16];
    public Obj3DShape(Context context) {
        super(context);

        obj = new Obj3D();
        try {
            ObjReader.read(context.getResources().getAssets().open("obj/hat.obj"), obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initProgram(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj.frag", context.getResources());
        programId = createGLProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void setModelMatrix(float[] matrix) {

    }

    @Override
    public void onSurfaceCreate(Context context) {

    }

    @Override
    public void onDraw() {
        LogUtils.i("onDraw");
        //调用此方法产生摄像机9参数位置矩阵
//        Matrix.setLookAtM(mVMatrix, 0, r*(float)Math.sin(angle),r*(float)Math.cos(angle) ,3 , 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        angle = angle + 0.01f;

        Matrix.multiplyMM(offsetTemp, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, offsetTemp, 0, offset, 0);

        Matrix.scaleM(mMatrix, 0, 0.2f, 0.2f * aspectRatio, 0.2f);

        Matrix.rotateM(mMatrix, 0, 90, 1, 0, 0);
        GLES20.glUseProgram(programId);
        GLES20.glEnableVertexAttribArray(LOCATION_POSITION);
        GLES20.glVertexAttribPointer(LOCATION_POSITION, 3, GLES20.GL_FLOAT, false, 3 * 4, obj.vert);
        GLES20.glEnableVertexAttribArray(LOCATION_NORMAL);
        GLES20.glVertexAttribPointer(LOCATION_NORMAL, 3, GLES20.GL_FLOAT, false, 3 * 4, obj.vertNorl);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.vertCount);
        GLES20.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);
        GLES20.glDisableVertexAttribArray(LOCATION_POSITION);
        GLES20.glDisableVertexAttribArray(LOCATION_NORMAL);
    }

    @Override
    public void onSurfaceChange(int width, int height) {
        aspectRatio = (width + 0f) / height;
        //调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mProjMatrix, 0, -aspectRatio * scale, aspectRatio * scale, -1 * scale, 1 * scale, 1, 10);

        if (temp) {
            Matrix.translateM(offset, 0, 1.0f, 0, 0);
        } else {
            Matrix.translateM(offset, 0, -1.0f, 0, 0);
        }


    }
}
