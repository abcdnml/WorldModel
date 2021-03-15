package com.aaa.worldmodel.surface;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;


import com.aaa.worldmodel.surface.shape.GLShape;
import com.aaa.worldmodel.surface.shape.Triangle;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRender implements GLSurfaceView.Renderer {
    private static final String TAG = WorldRender.class.getSimpleName();
    private int bgColor;
    private GLSurfaceView surfaceView;
    private List<GLShape> shapeList;

    public WorldRender(GLSurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public WorldRender(int bgColor) {
        this.bgColor = bgColor;
        shapeList = new ArrayList<>();
    }


    private void initShape() {
        float[] vertex = new float[]{
                0f, 0f, 0f,
                -1f, 1f, 0f,
                1f, 1f, 0f,
                1f, -1f, 0f
        };
        float[] color = new float[]{
                1f, 0f, 0f,
                1f, 1f, 0f,
                0f, 0f, 1f,
                0f, 1f, 1f
        };

        Triangle triangle = Triangle.newBuilder()
                .vertexBuffer(vertex)
                .colorBuffer(color)
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        addShape(triangle);
    }

    public void addShape(GLShape shape) {
        shapeList.add(shape);

    }

    public void remove(GLShape shape) {
        shapeList.remove(shape);
    }

    public void clear() {
        shapeList.clear();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        float bgRed = Color.red(bgColor) / 255f;
        float bgGreen = Color.green(bgColor) / 255f;
        float bgBlue = Color.blue(bgColor) / 255f;
        float bgAlpha = Color.alpha(bgColor) / 255f;
        GLES30.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);
        initShape();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        for (GLShape shape : shapeList) {
            shape.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        for (GLShape shape : shapeList) {
            shape.onDrawFrame(gl);
        }
    }
}
