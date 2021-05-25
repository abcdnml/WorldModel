package com.aaa.worldmodel.surface.obj;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.aaa.worldmodel.surface.GLDrawable;
import com.aaa.worldmodel.twodimensional.ShaderUtil;

public class ObjShape extends GLDrawable {
    private static final int LOCATION_POSITION = 0;
    private static final int LOCATION_NORMAL = 1;
    private static final int LOCATION_COORDINATE = 2;
    private static final int LOCATION_MAT_MODEL = 3;
    private static final int LOCATION_MAT_VIEW = 4;
    private static final int LOCATION_MAT_PROJ = 5;
    private static final int LOCATION_MAT_NORMAL = 6;


    static int programId;
    static String vertexShaderCode;
    static String fragmentShaderCode;

    float aspectRatio = 1;
    float scale = 1f;
    private int lightType = 2;

    private float[] modelMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] normalMatrix = new float[16];
    private float[] tempmatrix = new float[16];

    private Obj3D obj3D;

    public ObjShape(Context context, Obj3D obj3D, float scale) {
        super(context);
        this.obj3D = obj3D;
        this.scale = scale;
    }

    public static void initProgram(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj_mtl.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj_mtl.frag", context.getResources());
        programId = createGLProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    public void setModelMatrix(float[] matrix) {
        System.arraycopy(matrix, 0, modelMatrix, 0, matrix.length);
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale);

        Matrix.invertM(tempmatrix, 0, modelMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, tempmatrix, 0);
    }

    @Override
    public void onSurfaceCreate(Context context) {
        initProgram(context);
    }

    @Override
    public void onDraw() {
        GLES30.glUseProgram(programId);
//        Matrix.rotateM(mMatrix,0,0.3f,0,1,0);
        int location = GLES30.glGetAttribLocation(programId, "aPos");
        GLES30.glEnableVertexAttribArray(location);
        GLES30.glVertexAttribPointer(location, 3, GLES30.GL_FLOAT, false, 0, obj3D.vert);

        location = GLES30.glGetAttribLocation(programId, "aNormal");
        GLES30.glEnableVertexAttribArray(location);
        GLES30.glVertexAttribPointer(location, 3, GLES30.GL_FLOAT, false, 0, obj3D.vertNorl);


        if (obj3D.vertTexture != null) {
            location = GLES30.glGetAttribLocation(programId, "aTexCoords");
            GLES30.glEnableVertexAttribArray(location);
            GLES30.glVertexAttribPointer(location, 2, GLES30.GL_FLOAT, false, 0, obj3D.vertTexture);
        }

        location = GLES30.glGetUniformLocation(programId, "model");
        GLES30.glUniformMatrix4fv(location, 1, false, modelMatrix, 0);
        location = GLES30.glGetUniformLocation(programId, "view");
        GLES30.glUniformMatrix4fv(location, 1, false, mVMatrix, 0);
        location = GLES30.glGetUniformLocation(programId, "projection");
        GLES30.glUniformMatrix4fv(location, 1, false, mProjMatrix, 0);
        location = GLES30.glGetUniformLocation(programId, "normal_matrix");
        GLES30.glUniformMatrix4fv(location, 1, false, normalMatrix, 0);

        GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "material.ambient"), 1, obj3D.mtl.Ka, 0);
        GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "material.diffuse"), 1, obj3D.mtl.Kd, 0);
        GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "material.specular"), 1, obj3D.mtl.Ks, 0);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(programId, "material.shininess"), obj3D.mtl.Ns);

        if (lightType == 1) {
            GLES30.glUniform1i(GLES30.glGetUniformLocation(programId, "pointLight.attenuation"), 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "pointLight.position"), 1, new float[]{1f, 1f, 1f}, 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "pointLight.ambient"), 1, new float[]{1f, 1f, 1f}, 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "pointLight.diffuse"), 1, new float[]{1f, 1f, 1f}, 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "pointLight.specular"), 1, new float[]{1f, 1f, 1f}, 0);
            GLES30.glUniform1f(GLES30.glGetUniformLocation(programId, "pointLight.constant"), 0f);
            GLES30.glUniform1f(GLES30.glGetUniformLocation(programId, "pointLight.linear"), 0f);
            GLES30.glUniform1f(GLES30.glGetUniformLocation(programId, "pointLight.quadratic"), 0f);
        } else {
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "dirLight.direction"), 1, new float[]{-1f, -5f, 0f}, 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "dirLight.ambient"), 1, new float[]{0.75f, 0.75f, 1f}, 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "dirLight.diffuse"), 1, new float[]{1f, 1f, 1f}, 0);
            GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "dirLight.specular"), 1, new float[]{1f, 1f, 1f}, 0);
        }
        GLES30.glUniform1i(GLES30.glGetUniformLocation(programId, "lightType"), lightType);
        GLES30.glUniform3fv(GLES30.glGetUniformLocation(programId, "viewPos"), 1, new float[]{9f, 9f, 9f}, 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, obj3D.vertCount);

        GLES30.glDisableVertexAttribArray(LOCATION_POSITION);
        GLES30.glDisableVertexAttribArray(LOCATION_NORMAL);
    }

    @Override
    public void onSurfaceChange(int width, int height) {
        aspectRatio = (width + 0f) / height;
        Matrix.setLookAtM(mVMatrix, 0, 9, 9, 9, 0f, 0f, 0f, 0f, 1f, 0.0f);
        Matrix.perspectiveM(mProjMatrix, 0, 45, aspectRatio, 0.1f, 100);

        Matrix.invertM(tempmatrix, 0, modelMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, tempmatrix, 0);

    }
}
