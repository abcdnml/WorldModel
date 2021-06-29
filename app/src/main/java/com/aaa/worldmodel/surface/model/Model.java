package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.FloatBuffer;

public abstract class Model {

    protected int programId;
    protected String vertexShaderCode;
    protected String fragmentShaderCode;

    protected static final int FLOAT_SIZE = 4;
    private static final String TAG = Model.class.getSimpleName();
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer colorBuffer;
    protected Context context;

    public Model(Context context) {
        this.context = context;
    }


    public static float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }


    public abstract void setMatrix(float[] mMatrix, float[] vMatrix, float[] pMatrix);

    public void setEye(float[] eye) {

    }

    public void setLight(float[] light) {
    }

    public abstract void onSurfaceCreate(Context context);

    public abstract void onDraw();

    public abstract void onSurfaceChange(int width, int height);
}
