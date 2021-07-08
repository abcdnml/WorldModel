package com.aaa.worldmodel.surface.model;

import android.content.Context;

import java.nio.FloatBuffer;

public abstract class Model {

    protected static final int FLOAT_SIZE = 4;
    private static final String TAG = Model.class.getSimpleName();
    protected final float[] mMatrix = new float[16];
    protected final float[] pMatrix = new float[16];
    protected final float[] vMatrix = new float[16];
    protected int programId;
    protected String vertexShaderCode;
    protected String fragmentShaderCode;
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


    public void setMatrix(float[] mMatrix, float[] vMatrix, float[] pMatrix) {
        System.arraycopy(mMatrix, 0, this.mMatrix, 0, mMatrix.length);
        System.arraycopy(vMatrix, 0, this.vMatrix, 0, vMatrix.length);
        System.arraycopy(pMatrix, 0, this.pMatrix, 0, pMatrix.length);
    }

    public void setEye(float[] eye) {

    }

    public void setLight(float[] light) {
    }

    public abstract void onSurfaceCreate(Context context);

    public abstract void onDraw();

    public abstract void onSurfaceChange(int width, int height);
}
