package com.aaa.worldmodel.surface.shape;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Triangle extends GLShape {

    private static final String TAG = Triangle.class.getSimpleName();

    private static final int LOCATION_VERTEX = 0;
    private static final int LOCATION_COLOR = 1;

    private static final int VERTEX_SIZE = 3;
    private static final int COLOR_SIZE = 3;

    private static final int STRIDER = VERTEX_SIZE * 4;

    private final float[] mMatrix = new float[16];
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private int drawType = GLES30.GL_TRIANGLES;

    protected int programId;

    public Triangle() {
        vertexShaderCode = "# version 300 es \n" +
                "layout (location = 0) in vec4 vPosition;" +
                "layout (location = 1 ) in vec4 color;" +
                "uniform mat4 u_Matrix;" +
                "out vec4 fColor;" +
                "void main() {" +
                "     gl_Position  =  vPosition;" +
//                "     gl_Position  = u_Matrix * vPosition;" +
                "     gl_PointSize = 100.0;" +
                "     fColor = color;" +
                "}";

        fragmentShaderCode = "# version 300 es \n" +
                "precision mediump float;" +
                "out vec4 fragColor;" +
                "in vec4 fColor;" +
                "void main() {" +
                "     fragColor = fColor;" +
                "}";
    }

    public Triangle(float[] vertex, float[] color, int type) {
        this();
        setTriangleVertex(vertex);
        setTriangleVertexColor(color);
        setDrawType(type);
        createGLProgram();
    }

    private Triangle(Builder builder) {
        this(builder.vertex, builder.color, builder.drawType);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public void setTriangleVertex(float[] vertex) {
        if (/*vertex.length > 9 && */vertex.length % VERTEX_SIZE == 0) {
            vertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(vertex);
            vertexBuffer.flip();
        } else {
            Log.e(TAG, "vertex length not correct");
        }
    }

    public void setTriangleVertexColor(float[] color) {
        if (/*color.length > 9 && */color.length % COLOR_SIZE == 0) {
            colorBuffer = ByteBuffer.allocateDirect(color.length * 4)
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

    private void createGLProgram() {
        int vertexShaderId = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderId = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        if (vertexShaderId == 0 || fragmentShaderId == 0) {
            Log.e(TAG, " shader id is 0  vertex :" + vertexShaderId + " color: " + fragmentShaderId);
            return;
        }

        programId = linkProgram(vertexShaderId, fragmentShaderId);
        if (programId == 0) {
            Log.e(TAG, " program id is 0");
            return;
        }

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
//        if (width > height) {
//            //横屏
//            Matrix.orthoM(mMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            //竖屏
//            Matrix.orthoM(mMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glUseProgram(programId);
        GLES30.glEnableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glEnableVertexAttribArray(LOCATION_COLOR);
//        GLES30.glUniformMatrix4fv(LOCATION_MATRIX, 1, false, mMatrix, 0);

        GLES30.glVertexAttribPointer(LOCATION_VERTEX, 3, GLES30.GL_FLOAT, false, 4 * VERTEX_SIZE, vertexBuffer);
        GLES30.glVertexAttribPointer(LOCATION_COLOR, 3, GLES30.GL_FLOAT, false, 4 * VERTEX_SIZE, colorBuffer);

//        GLES30.glDrawArrays(drawType, 0, vertexBuffer.capacity());
        Log.i(TAG, "vertexBuffer.capacity() : " + vertexBuffer.capacity());
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vertexBuffer.capacity());
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, vertexBuffer.capacity());

        GLES30.glDisableVertexAttribArray(LOCATION_VERTEX);
        GLES30.glDisableVertexAttribArray(LOCATION_COLOR);
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
