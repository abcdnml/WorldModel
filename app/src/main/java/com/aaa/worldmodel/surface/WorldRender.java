package com.aaa.worldmodel.surface;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;


import com.aaa.worldmodel.surface.shape.Cube;
import com.aaa.worldmodel.surface.shape.Triangle;
import com.aaa.worldmodel.surface.texture.Texture2D;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRender implements GLSurfaceView.Renderer {
    private static final String TAG = WorldRender.class.getSimpleName();
    private int bgColor;
    private GLSurfaceView surfaceView;
    private volatile List<GLDrawable> shapeList;

    public WorldRender(GLSurfaceView surfaceView, int bgColor) {
        this.bgColor = bgColor;
        this.surfaceView = surfaceView;
        shapeList = new ArrayList<>();
    }


    public void addShape(final GLDrawable shape) {
        Log.i(TAG, "addShape");
        shapeList.add(shape);
    }

    public void remove(GLDrawable shape) {
        shapeList.remove(shape);
    }

    public void clear() {
        shapeList.clear();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: " );
        float bgRed = Color.red(bgColor) / 255f;
        float bgGreen = Color.green(bgColor) / 255f;
        float bgBlue = Color.blue(bgColor) / 255f;
        float bgAlpha = Color.alpha(bgColor) / 255f;
        GLES30.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        Triangle.initProgram();
        Cube.initProgram();
        Texture2D.initProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged width: " + width+" height : "+height);
        GLES30.glViewport(0, 0, width, height);
        for (GLDrawable shape : shapeList) {
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

}
