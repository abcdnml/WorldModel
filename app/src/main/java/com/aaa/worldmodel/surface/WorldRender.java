package com.aaa.worldmodel.surface;

import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.utils.LogUtils;
import com.aaa.worldmodel.utils.MatrixUtils;
import com.aaa.worldmodel.utils.PointF3;
import com.aaa.worldmodel.utils.PointF4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRender implements GLSurfaceView.Renderer {
    private static final String TAG = WorldRender.class.getSimpleName();
    private static final float MAX_SCALE = 4;
    private static final float MIN_SCALE = 0.25f;
    final float TOUCH_SCALE_AC = 5;
    private int bgColor;
    private GLSurfaceView surfaceView;
    private volatile List<GLDrawable> shapeList;
    private float[] modelMatrix = GLDrawable.getOriginalMatrix();
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] eye = new float[9];
    private float rotateX = 0;
    private float rotateY = 0;
    private float scale = 1;

    public WorldRender(GLSurfaceView surfaceView, int bgColor) {
        this.bgColor = bgColor;
        this.surfaceView = surfaceView;
        shapeList = new ArrayList<>();
    }

    public void addShape(final GLDrawable shape) {
        Log.i(TAG, "addShape");
        shapeList.add(shape);
        shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
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

        for (GLDrawable shape : shapeList) {
            shape.onSurfaceCreate(surfaceView.getContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged width: " + width + " height : " + height);
        GLES30.glViewport(0, 0, width, height);

        float aspectRatio = (width + 0f) / height;
        //眼睛坐标和法向量一定要算好 要不然 看到别的地方去了
        Matrix.setLookAtM(mVMatrix, 0, 0, 9, 0, 0f, 0f, 0f, 0f, 0f, -1f);
        Matrix.perspectiveM(mProjMatrix, 0, 90, aspectRatio, 0.1f, 100);
        for (GLDrawable shape : shapeList) {
            shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
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

    //平移  平移某个模型 还是平移视角  双指平移视角?
    public void translate() {

    }

    //旋转
    public void rotate(float distanceX, float distanceY) {
        rotateX = rotateX + distanceX;
        Log.i(TAG, "onScroll rotateX : " + rotateX + "  rotateY: " + rotateY);
        if (rotateY + distanceY > 90 * TOUCH_SCALE_AC || rotateY + distanceY < 0) {
            distanceY = 0;
        } else {
            rotateY = rotateY + distanceY;
        }

        Matrix.setRotateM(modelMatrix, 0, -rotateY / TOUCH_SCALE_AC, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, -rotateX / TOUCH_SCALE_AC, 0, 1, 0);
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale);

        for (GLDrawable shape : shapeList) {
            shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
        }
        surfaceView.requestRender();
    }

    public void scale(float s) {
        //这样写可以造成一个缩放回弹的效果 回弹效果要在scaleEnd时重新设置回边界大小
        Matrix.scaleM(modelMatrix, 0, s, s, s);
        scale = scale * s;
        for (GLDrawable shape : shapeList) {
            shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
        }
        surfaceView.requestRender();
    }

    public void onScaleEnd(float s) {
        float tempScale = scale * s;
        LogUtils.i("scale end ");
        if (tempScale > MAX_SCALE) {
            s = MAX_SCALE / scale;
            scale = MAX_SCALE;
        } else if (tempScale < MIN_SCALE) {
            s = MIN_SCALE / scale;
            scale = MIN_SCALE;
        } else {
            scale = tempScale;
        }
        Matrix.scaleM(modelMatrix, 0, s, s, s);

        for (GLDrawable shape : shapeList) {
            shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
        }
        surfaceView.requestRender();
    }

    public PointF getScreenPointBy3d(float x, float y, float z) {
        float[] temp1 = new float[16];
        float[] temp2 = new float[16];
        float[] result = new float[4];
        LogUtils.i("press: " + new PointF3(x, y, z).toString());
        float[] input=new float[]{x,y,z,1};


        Matrix.multiplyMM(temp1, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(temp2, 0, temp1, 0, modelMatrix, 0);
        Matrix.multiplyMV(result, 0,  temp2,  0,input,0);
        LogUtils.i(" matrix result  "+ Arrays.toString(result) );

        float w = surfaceView.getWidth();
        float h = surfaceView.getHeight();
        PointF4 pointF4 = MatrixUtils.getTranslatePoint(temp2, new PointF3(x, y, z));
        LogUtils.i("result p4 " + pointF4);
        PointF p = new PointF(pointF4.x * w / 2 +w / 2, h / 2 * pointF4.y + h / 2);
        LogUtils.i("result screen" + p);
        return p;
    }

    public void get3DPointByScreen(float x, float y) {
        float[] temp1 = new float[16];
        float[] temp2 = new float[16];
        float[] temp3 = new float[16];

        float w = surfaceView.getWidth();
        float h = surfaceView.getHeight();

        Matrix.multiplyMM(temp1, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.multiplyMM(temp2, 0, temp1, 0, modelMatrix, 0);
        Matrix.invertM(temp3, 0, temp2, 0);
        LogUtils.i("result:x y : " + x + ", " + y);

        PointF4 pointF4 = MatrixUtils.getTranslatePoint(temp2, new PointF3(x / (w / 2) - 1, 1, y / (h / 2) - 1));
        LogUtils.i("result: " + pointF4.toString());

    }

    public void get3DPointByScreen1(float x, float y) {
        int w = surfaceView.getWidth();
        int h = surfaceView.getHeight();
        LogUtils.i("screen:x y : " + x + ", " + y);
        float[] xyz = MatrixUtils.unProject((int) x, (int) y, w, h, mProjMatrix, modelMatrix);
        LogUtils.i("result:xyz: " + xyz[0] + "," + xyz[1] + ", " + xyz[2]);

    }
}
