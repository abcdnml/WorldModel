package com.aaa.worldmodel.surface.shape;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.surface.GLDrawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class Cube extends GLDrawable {

    private static final int LOCATION_VERTEX = 0;
    private static final int LOCATION_COLOR = 1;
    private static final int LOCATION_MATRIX = 2;
    private static final String TAG = Cube.class.getSimpleName();

    protected static String vertexShaderCode = "# version 300 es \n" +
            "layout (location = 0) in vec4 vPosition;" +
            "layout (location = 1) in vec4 color;" +
            "layout (location = 2) uniform mat4 u_Matrix;" +
            "out vec4 fColor;" +
            "void main() {" +
            "     gl_Position  = u_Matrix * vPosition;" +
            "     gl_PointSize = 100.0;" +
            "     fColor = color;" +
            "}";
    protected static String fragmentShaderCode = "# version 300 es \n" +
            "precision mediump float;" +
            "out vec4 fragColor;" +
            "in vec4 fColor;" +
            "void main() {" +
            "     fragColor = fColor;" +
            "}";

    private final float[] mMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    protected static int programId;

    private static final int VERTEX_SIZE = 3;
    private static final int COLOR_SIZE = 3;
    private IntBuffer indexBuffer;

    public Cube(Context context,float[] vertex, int[] indexs, float[] color) {
        super(context);
        setVertex(vertex);
        setVertexColor(color);
        setVertexIndex(indexs);
    }

    public void setVertex(float[] vertex) {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertex);
        vertexBuffer.flip();
    }

    public void setVertexColor(float[] color) {
        colorBuffer = ByteBuffer.allocateDirect(color.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(color);
        colorBuffer.flip();
    }


    public void setVertexIndex(int[] index) {
        indexBuffer = ByteBuffer.allocateDirect(index.length * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer().put(index);
        indexBuffer.flip();
    }

    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix,float[] pMatrix) {

    }

    @Override
    public void onSurfaceCreate(Context context) {
        programId = createGLProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            Log.e(TAG, "Program id is 0 ,may not init");
            return;
        }
        Log.i(TAG, "onDrawFrame cube programId: " + programId);
        GLES30.glUseProgram(programId);
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);
        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);

        GLES30.glVertexAttribPointer(LOCATION_VERTEX, VERTEX_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, vertexBuffer);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, COLOR_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, colorBuffer);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexBuffer.capacity(), GLES30.GL_UNSIGNED_INT, indexBuffer);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vertexBuffer.capacity() / 3);

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glDisableVertexAttribArray(LOCATION_COLOR);
    }

    @Override
    public void onSurfaceChange(int width, int height) {
        final float aspectRatio = (width + 0f) / height;
        //调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mProjMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 10);
        //调用此方法产生摄像机9参数位置矩阵
        Matrix.setLookAtM(mVMatrix, 0, -3, 3, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
    }

}
