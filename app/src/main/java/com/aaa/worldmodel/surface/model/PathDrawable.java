package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import com.aaa.worldmodel.surface.obj.Path3D;
import com.aaa.worldmodel.utils.LogUtils;
import com.aaa.worldmodel.utils.ShaderUtil;

public class PathDrawable extends Model {

    private float[] modelMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private int LOCATION_VETEX;
    private int LOCATION_MAT_COLOR;
    private int LOCATION_MAT_MODEL;
    private int LOCATION_MAT_VIEW;
    private int LOCATION_MAT_PROJ;

    private int[] vao = new int[1];

    private Path3D path3D;

    public PathDrawable(Context context) {
        super(context);
    }

    public PathDrawable(Context context, Path3D path3D) {
        super(context);
        this.path3D = path3D;
    }

    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix, float[] pMatrix) {
        System.arraycopy(mMatrix, 0, modelMatrix, 0, mMatrix.length);
        System.arraycopy(vMatrix, 0, mVMatrix, 0, vMatrix.length);
        System.arraycopy(pMatrix, 0, mProjMatrix, 0, pMatrix.length);
    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/path.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/path.frag", context.getResources());
        programId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        initLocation();
        initVAO();
    }

    private void initLocation() {
        LOCATION_VETEX = GLES30.glGetAttribLocation(programId, "aPos");
        LOCATION_MAT_COLOR = GLES30.glGetUniformLocation(programId, "color");
        LOCATION_MAT_MODEL = GLES30.glGetUniformLocation(programId, "model");
        LOCATION_MAT_VIEW = GLES30.glGetUniformLocation(programId, "view");
        LOCATION_MAT_PROJ = GLES30.glGetUniformLocation(programId, "projection");
    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            LogUtils.e( "Program id is 0 ,may not init");
            return;
        }
        LogUtils.i( "onDraw path  programId: " + programId);
        GLES30.glUseProgram(programId);
        GLES30.glLineWidth(4);
        GLES30.glUniform3fv(LOCATION_MAT_COLOR, 1, path3D.color, 0);

        GLES30.glUniformMatrix4fv(LOCATION_MAT_MODEL, 1, false, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_MAT_VIEW, 1, false, mVMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_MAT_PROJ, 1, false, mProjMatrix, 0);

//        drawNormaly();
        drawWithVAO();

    }

    private void drawNormaly() {
        GLES30.glEnableVertexAttribArray(LOCATION_VETEX);
        GLES30.glVertexAttribPointer(LOCATION_VETEX, 3, GLES30.GL_FLOAT, false, 0, path3D.vert);
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, path3D.vertCount);
//        GLES30.glDisableVertexAttribArray(GLES30.glGetAttribLocation(programId, "aPos"));
    }

    private void initVAO() {
        GLES30.glGenVertexArrays(1, vao, 0);

        GLES30.glBindVertexArray(vao[0]);
        int[] vbo = new int[1];
        GLES30.glGenBuffers(1, vbo, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, path3D.vert.capacity() * 4, path3D.vert, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(LOCATION_VETEX, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(LOCATION_VETEX);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
    }

    private void drawWithVAO() {
        GLES30.glBindVertexArray(vao[0]);
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, path3D.vertCount);
        GLES30.glBindVertexArray(GLES30.GL_NONE);
    }

    @Override
    public void onSurfaceChange(int width, int height) {

    }
}
