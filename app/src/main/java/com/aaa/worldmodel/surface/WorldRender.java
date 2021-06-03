package com.aaa.worldmodel.surface;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRender implements GLSurfaceView.Renderer {
    private static final String TAG = WorldRender.class.getSimpleName();
    private int bgColor;
    private GLSurfaceView surfaceView;
    private volatile List<GLDrawable> shapeList;
    private float[] modelMatrix = GLDrawable.getOriginalMatrix();
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] eye = new float[9];

    public WorldRender(GLSurfaceView surfaceView, int bgColor) {
        this.bgColor = bgColor;
        this.surfaceView = surfaceView;
        shapeList = new ArrayList<>();
    }


    public void addShape(final GLDrawable shape) {
        Log.i(TAG, "addShape");
        shapeList.add(shape);
        shape.setMatrix(modelMatrix,mVMatrix,mProjMatrix);
    }

    public void remove(GLDrawable shape) {
        shapeList.remove(shape);
    }

    public void clear() {
        shapeList.clear();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: ");
        float bgRed = Color.red(bgColor) / 255f;
        float bgGreen = Color.green(bgColor) / 255f;
        float bgBlue = Color.blue(bgColor) / 255f;
        float bgAlpha = Color.alpha(bgColor) / 255f;
        GLES30.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE_MODE);

        for(GLDrawable shape:shapeList){
            shape.onSurfaceCreate(surfaceView.getContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged width: " + width + " height : " + height);
        GLES30.glViewport(0, 0, width, height);

        float aspectRatio = (width + 0f) / height;
        //眼睛坐标和法向量一定要算好 要不然 看到别的地方去了
        Matrix.setLookAtM(mVMatrix, 0, 0, 9, 0, 0f, 0f, 0f, 0f, 0f, -1.0f);
        Matrix.perspectiveM(mProjMatrix, 0, 90, aspectRatio, 0.1f, 100);
        for (GLDrawable shape : shapeList) {
            shape.setMatrix(modelMatrix,mVMatrix,mProjMatrix);
            shape.onSurfaceChange(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Log.i(TAG, "onDrawFrame width: ");
        for (GLDrawable shape : shapeList) {
            shape.onDraw();
        }
    }

    //平移  平移某个模型 还是平移视角
    public void translate() {

    }

    private float rotateX = 0;
    private float rotateY = 0;
    float touchScale =5;

    //旋转
    public void rotate(float distanceX, float distanceY) {
        rotateX = rotateX + distanceX;
        Log.i(TAG, "onScroll rotateX : " + rotateX + "  rotateY: " + rotateY);
        if (rotateY + distanceY > 90* touchScale || rotateY + distanceY <0) {
            distanceY = 0;
        } else {
            rotateY = rotateY + distanceY;
        }

        Matrix.setRotateM(modelMatrix, 0, -rotateY / touchScale, 1, 0, 0);
        Matrix.rotateM(modelMatrix,0, -rotateX / touchScale, 0, 1, 0);

        for (GLDrawable shape : shapeList) {
            shape.setMatrix(modelMatrix,mVMatrix,mProjMatrix);
        }
        surfaceView.requestRender();
    }

    public void scale(float scaleX,float scaleY){

    }
}
