package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.utils.ShaderUtil;
import com.aaa.worldmodel.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class TestModel extends Model {

    private static final String TAG = Cube.class.getSimpleName();
    private static final int VERTEX_SIZE = 3;
    private static final int COLOR_SIZE = 3;
    private static int LOCATION_VERTEX = 0;
    private static int LOCATION_COLOR = 1;
    private static int LOCATION_MATRIX = 2;
    private final float[] mMatrix = new float[16];
    private final float[] pMatrix = new float[16];
    private final float[] vMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
    private final float[] tempMatrix = new float[16];
    int[] vao = new int[1];
    int[] vbo = new int[3];
    private IntBuffer indexBuffer;

    public TestModel(Context context, float[] vertex, int[] indexs, float[] color) {
        super(context);
        setVertex(vertex, color, indexs);
    }

    public void setVertex(float[] vertex, float[] color, int[] index) {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertex);
        vertexBuffer.flip();
        LogUtils.i("vertex buffer size " + vertexBuffer.capacity());
        colorBuffer = ByteBuffer.allocateDirect(color.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(color);
        colorBuffer.flip();
        indexBuffer = ByteBuffer.allocateDirect(index.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer().put(index);
        indexBuffer.flip();
    }

    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix, float[] pMatrix) {
        System.arraycopy(mMatrix, 0, this.mMatrix, 0, mMatrix.length);
        System.arraycopy(vMatrix, 0, this.vMatrix, 0, vMatrix.length);
        System.arraycopy(pMatrix, 0, this.pMatrix, 0, pMatrix.length);
        conbineMatrix();

    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/triangle.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/triangle.frag", context.getResources());
        programId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);


        LOCATION_VERTEX = GLES20.glGetAttribLocation(programId, "aPosition");
        LOCATION_COLOR = GLES20.glGetAttribLocation(programId, "aColor");
        LOCATION_MATRIX = GLES20.glGetUniformLocation(programId, "uMVPMatrix");

//        initVBO();
//        initVAO();

    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            Log.e(TAG, "Program id is 0 ,may not init");
            return;
        }
        Log.i(TAG, "onDrawFrame  programId: " + programId);
        GLES30.glUseProgram(programId);

        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mvpMatrix, 0);


        drawNormaly();

//        drawWithVBO();

//        drawWithVAO();

    }

    private void drawNormaly() {
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glVertexAttribPointer(LOCATION_VERTEX, VERTEX_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, vertexBuffer);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, COLOR_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, colorBuffer);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexBuffer.capacity()/2, GLES30.GL_UNSIGNED_INT, indexBuffer);
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0,vertexBuffer.capacity()/3);

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glDisableVertexAttribArray(LOCATION_COLOR);
    }

    private void  initVBO(){
        GLES30.glGenBuffers(3, vbo, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, colorBuffer.capacity() * 4, colorBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 4, indexBuffer, GLES30.GL_STATIC_DRAW);

    }
    private void drawWithVBO() {
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glVertexAttribPointer(LOCATION_VERTEX, 3, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, 0);

        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1]);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, 3, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[2]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,  indexBuffer.capacity(), GLES30.GL_UNSIGNED_INT, 0);
    }
    private void initVAO(){
        GLES30.glGenVertexArrays(1,vao,0);
        GLES30.glBindVertexArray(vao[0]);

        GLES30.glGenBuffers(3, vbo, 0);

        GLES30.glBindVertexArray(vao[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(LOCATION_VERTEX, 3, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, 0);
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, colorBuffer.capacity() * 4, colorBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, 3, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, 0);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 4, indexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindVertexArray(GLES30.GL_NONE);
    }

    private void drawWithVAO() {
        GLES30.glBindVertexArray(vao[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexBuffer.capacity(), GLES30.GL_UNSIGNED_INT, 0);
        GLES30.glBindVertexArray(GLES30.GL_NONE);
    }

    @Override
    public void onSurfaceChange(int width, int height) {
    }

    private void conbineMatrix(){
        Matrix.multiplyMM(tempMatrix, 0, pMatrix, 0, vMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, tempMatrix, 0, mMatrix, 0);
    }
}
