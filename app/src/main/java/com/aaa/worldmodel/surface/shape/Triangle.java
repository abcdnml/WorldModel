package com.aaa.worldmodel.surface.shape;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.aaa.worldmodel.surface.GLDrawable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Triangle extends GLDrawable {

    private static final String TAG = Triangle.class.getSimpleName();

    private static final int LOCATION_VERTEX = 0;
    private static final int LOCATION_COLOR = 1;
    private static final int LOCATION_MATRIX = 2;

    private static final int VERTEX_SIZE = 3;
    private static final int COLOR_SIZE = 3;



    private final float[] mMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];

    private int drawType = GLES30.GL_TRIANGLES;
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
    protected static int programId;


    public Triangle(float[] vertex, float[] color, int type) {
        setTriangleVertex(vertex);
        setTriangleVertexColor(color);
        setDrawType(type);
    }

    private Triangle(Builder builder) {
        this(builder.vertex, builder.color, builder.drawType);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void setTriangleVertex(float[] vertex) {
        if (/*vertex.length > 9 && */vertex.length % VERTEX_SIZE == 0) {
            vertexBuffer = ByteBuffer.allocateDirect(vertex.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(vertex);
            vertexBuffer.flip();
        } else {
            Log.e(TAG, "vertex length not correct");
        }
    }

    public void setTriangleVertexColor(float[] color) {
        if (/*color.length > 9 && */color.length % COLOR_SIZE == 0) {
            colorBuffer = ByteBuffer.allocateDirect(color.length * FLOAT_SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(color);
            colorBuffer.flip();
        } else {
            Log.e(TAG, "vertex color length not correct");
        }
    }

    public void setDrawType(int type) {
        if (type == GLES30.GL_TRIANGLES || type == GLES30.GL_TRIANGLE_STRIP || type == GLES30.GL_TRIANGLE_FAN) {
            drawType = type;
        } else {
            Log.e(TAG, "DrawType not correct, should be  GLES30.GL_TRIANGLES ，GLES30.GL_TRIANGLE_STRIP or GLES30.GL_TRIANGLE_FAN！");
        }
    }

    @Override
    public void onSurfaceChange(int width, int height) {
/*        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            //横屏
            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            //竖屏
            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }*/
        final float aspectRatio = (width + 0f) / height;
        //调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mProjMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 10);
        //调用此方法产生摄像机9参数位置矩阵
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
    }

    @Override
    public void onDraw() {
        if (programId == 0) {
            Log.e(TAG, "Program id is 0 ,may not init");
            return;
        }
        GLES30.glUseProgram(programId);
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);
        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);

        GLES30.glVertexAttribPointer(LOCATION_VERTEX, VERTEX_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * VERTEX_SIZE, vertexBuffer);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, COLOR_SIZE, GLES30.GL_FLOAT, false, FLOAT_SIZE * COLOR_SIZE, colorBuffer);

        GLES30.glDrawArrays(drawType, 0, vertexBuffer.capacity() / VERTEX_SIZE);
//        Log.i(TAG, "vertexBuffer.capacity() : " + vertexBuffer.capacity());
//        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vertexBuffer.capacity()/VERTEX_SIZE);
//        GLES30.glDrawArrays(GLES30.GL_LINES, 0, vertexBuffer.capacity()/VERTEX_SIZE);

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glDisableVertexAttribArray(LOCATION_COLOR);
    }

    public static void initProgram() {
        programId = createGLProgram(vertexShaderCode, fragmentShaderCode);
    }

    public static final class Builder {
        private float[] vertex;
        private float[] color;
        private int drawType;

        private Builder() {
        }

        public Builder vertexBuffer(float[] vertexBuffer) {
            this.vertex = vertexBuffer;
            return this;
        }

        public Builder colorBuffer(float[] colorBuffer) {
            this.color = colorBuffer;
            return this;
        }

        public Builder drawType(int drawType) {
            this.drawType = drawType;
            return this;
        }

        public Triangle build() {
            if (/*vertex.length <= 9 ||*/ vertex.length % 3 != 0) {
                throw new IllegalArgumentException("vertex length not correct! ");
            }
            if (/*color.length <= 9 ||*/ color.length % 3 != 0) {
                throw new IllegalArgumentException("vertex length not correct! ");
            }
            if (drawType != GLES30.GL_TRIANGLES && drawType != GLES30.GL_TRIANGLE_STRIP && drawType != GLES30.GL_TRIANGLE_FAN) {
                throw new IllegalArgumentException("vertex length not correct! ");
            }

            return new Triangle(this);
        }
    }
}
