package com.aaa.worldmodel.surface;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.move.MoveManager;
import com.aaa.worldmodel.move.MovementListener;
import com.aaa.worldmodel.surface.model.Model;
import com.aaa.worldmodel.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRender implements GLSurfaceView.Renderer {
    private static final String TAG = WorldRender.class.getSimpleName();
    private static final float MAX_SCALE = 4;
    private static final float MIN_SCALE = 0.25f;
    final float TOUCH_SCALE_AC = 5;
    private int bgColor = Color.TRANSPARENT;
    private GLSurfaceView surfaceView;
    private volatile List<Model> shapeList;
    private float[] modelMatrix = Model.getOriginalMatrix();
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] light;
    private float rotateX = 0;
    private float rotateY = 0;
    private float scale = 1;
    private MoveManager moveManager;

    public WorldRender(GLSurfaceView surfaceView, int bgColor) {
        this.bgColor = bgColor;
        this.surfaceView = surfaceView;
        this.moveManager = new MoveManager();
        moveManager.setMovementListener(new MovementListener() {
            @Override
            public void onMove(float positionX, float positionY, float positionZ, float directionX, float directionY, float directionZ) {
                LogUtils.i("position: (" + positionX + "," + positionY + "," + positionZ + ")");
                LogUtils.i("direction: (" + directionX + "," + directionY + "," + directionZ + ")");
            }
        });
        init();
    }

    private void init() {
        shapeList = new ArrayList<>();
        light = new float[]{
                -1f, -8f, 0f,       // direction  x y z
                0.5f, 0.5f, 0.8f,   // ka
                0.6f, 0.8f, 0.6f,   // kd
                0.5f, 0.5f, 0.5f,   // ks
        };

    }

    public void addShape(final Model shape) {
        Log.i(TAG, "addShape" + shape.getClass().getName());
        shapeList.add(shape);
        shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
        shape.setEye(moveManager.getPosition());
        shape.setLight(light);
    }

    public void remove(Model shape) {
        shapeList.remove(shape);
    }

    public void clear() {
        shapeList.clear();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated: ");
        setBackgroundColor(bgColor);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE_MODE);

        for (Model shape : shapeList) {
            shape.onSurfaceCreate(surfaceView.getContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged width: " + width + " height : " + height);
        GLES30.glViewport(0, 0, width, height);

        float[] eye = moveManager.getPosition();
        //??????????????????????????????????????? ????????? ????????????????????????
        Matrix.setLookAtM(mVMatrix, 0, eye[0], eye[1], eye[2], 0, 0, 0, 0, 1, 0);
        Matrix.perspectiveM(mProjMatrix, 0, 90, (width + 0f) / height, 0.1f, 100);
        for (Model shape : shapeList) {
            shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
            shape.onSurfaceChange(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Log.i(TAG, "onDrawFrame width: ");
        for (Model shape : shapeList) {
            shape.onDraw();
        }
    }

    //??????
    public void rotateWorld(float distanceX, float distanceY) {
        rotateX = rotateX + distanceX;
        rotateY = rotateY + distanceY;

        Matrix.setRotateM(modelMatrix, 0, -rotateY / TOUCH_SCALE_AC, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, -rotateX / TOUCH_SCALE_AC, 0, 1, 0);
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale);

        updateModel();
    }

    public void scale(float s) {
        //???????????????????????????????????????????????? ??????????????????scaleEnd??????????????????????????????
        Matrix.scaleM(modelMatrix, 0, s, s, s);
        scale = scale * s;
        updateModel();
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

        updateModel();
    }

    /**
     * ????????????
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        this.bgColor = color;
        float bgRed = Color.red(bgColor) / 255f;
        float bgGreen = Color.green(bgColor) / 255f;
        float bgBlue = Color.blue(bgColor) / 255f;
        float bgAlpha = Color.alpha(bgColor) / 255f;
        GLES30.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);
    }

    public void move(float distanceX, float distanceY, float distanceZ) {
        mVMatrix = moveManager.move(distanceX, distanceY, distanceZ);
        updateModel();
    }

    public void move(float distance) {
        mVMatrix = moveManager.moveDirection(distance);
        updateModel();
    }

    public void moveTo(float x, float y, float z) {
        mVMatrix = moveManager.moveTo(x, y, z);
        updateModel();
    }

    /**
     * ??????????????????
     *
     * @param centerX
     * @param centerY
     * @param degreeX
     * @param degreeY
     * @param degreeZ
     */
    public void surround(float centerX, float centerY, float degreeX, float degreeY, float degreeZ) {

    }

    public void moveSelf(float distanceX, float distanceY, float distanceZ) {

    }

    /**
     * ?????????(Pitch)????????????(Yaw)????????????(Roll)
     *
     * @param rotateX
     * @param rotateY
     * @param rotateZ
     */
    public void rotateSelf(float pitch, float yaw, float roll) {
        mVMatrix=moveManager.rotate(pitch, yaw, roll);
        updateModel();
    }

    private void updateModel(){
        for (Model shape : shapeList) {
            shape.setMatrix(modelMatrix, mVMatrix, mProjMatrix);
        }
        surfaceView.requestRender();
    }
}
