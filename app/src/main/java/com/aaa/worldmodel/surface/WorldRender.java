package com.aaa.worldmodel.surface;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.LDMapBean;
import com.aaa.worldmodel.surface.obj.MtlInfo;
import com.aaa.worldmodel.surface.obj.Obj3D;
import com.aaa.worldmodel.surface.obj.ObjShape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
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

    public WorldRender(GLSurfaceView surfaceView, int bgColor) {
        this.bgColor = bgColor;
        this.surfaceView = surfaceView;
        shapeList = new ArrayList<>();
    }


    public void addShape(final GLDrawable shape) {
        Log.i(TAG, "addShape");
        shapeList.add(shape);
        shape.setModelMatrix(modelMatrix);
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
//        Triangle.initProgram();
//        Cube.initProgram();
//        Texture2D.initProgram();
//        Obj3DShape.initProgram(surfaceView.getContext());
//        Obj3DShape1.initProgram(surfaceView.getContext());
//        ImageHandle.initProgram(surfaceView.getContext());
        ObjShape.initProgram(surfaceView.getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged width: " + width + " height : " + height);
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

    //平移  平移某个模型 还是平移视角
    public void translate() {

    }

    public void setMapData(LDMapBean ldMapBean) {
    }


    //缩放
    //旋转
    public void rotate(float x, float y) {
        Matrix.rotateM(modelMatrix, 0, -x/5, 0, 1, 0);
//        Matrix.rotateM(modelMatrix, 0, -y, 1, 0, 0);
        for (GLDrawable shape : shapeList) {
            shape.setModelMatrix(modelMatrix);
        }
        surfaceView.requestRender();
    }
}
