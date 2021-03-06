package com.aaa.worldmodel.surface.model;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.SparseArray;

import com.aaa.worldmodel.surface.obj.Obj3D;
import com.aaa.worldmodel.utils.ShaderUtil;

import java.util.ArrayList;
import java.util.List;

public class ObjModel extends Model {
    //模型放置到地图上时 本身需要做平移缩放旋转
    private float scale = 1f;
    private float offsetX = 0f;
    private float offsetY = 0f;
    private float offsetZ = 0f;
    private float rotateX = 0f;
    private float rotateY = 0f;
    private float rotateZ = 0f;
    private int[] vao;
    private int LOCATION_VETEX;
    private int LOCATION_NORMAL;
    private int LOCATION_TEXTURE;
    private int LOCATION_MAT_MODEL;
    private int LOCATION_MAT_VIEW;
    private int LOCATION_MAT_PROJ;
    private int LOCATION_MAT_NORMAL;
    private int LOCATION_MTL_KA;
    private int LOCATION_MTL_KD;
    private int LOCATION_MTL_KS;
    private int LOCATION_MTL_NS;
    private int LOCATION_LIGHT_DIR;
    private int LOCATION_LIGHT_KA;
    private int LOCATION_LIGHT_KD;
    private int LOCATION_LIGHT_KS;
    private int LOCATION_EYE_POS;
    private float[] modelMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] normalMatrix = new float[16];
    private float[] tempmatrix = new float[16];
    private float[] eye = new float[9];
    private float[] light = new float[12];
    private List<Obj3D> obj3Ds = new ArrayList<>();
    private SparseArray<Obj3D> objVao = new SparseArray<>();


    public ObjModel(Context context, List<Obj3D> obj3Ds) {
        super(context);
        this.obj3Ds = obj3Ds;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setOffset(float offsetX, float offsetY, float offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public void setRotate(float rotateX, float rotateY, float rotateZ) {
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
    }

    @Override
    public void setMatrix(float[] mMatrix, float[] vMatrix, float[] pMatrix) {
        super.setMatrix(mMatrix,vMatrix,pMatrix);

        Matrix.translateM(modelMatrix, 0, offsetX, offsetY, offsetZ);

        Matrix.scaleM(modelMatrix, 0, scale, scale, scale);

        //旋转要放到平移后面  旋转的中心点就在000  否则 ....
        Matrix.rotateM(modelMatrix, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, rotateZ, 0, 0, 1);


        Matrix.invertM(tempmatrix, 0, modelMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, tempmatrix, 0);
    }

    @Override
    public void setEye(float[] eye) {
        this.eye = eye;
    }

    @Override
    public void setLight(float[] light) {
        this.light = light;
    }

    @Override
    public void onSurfaceCreate(Context context) {
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj_mtl.vert", context.getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("shader/obj_mtl.frag", context.getResources());
        programId = ShaderUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        initLocation();

        vao=new int[obj3Ds.size()];
        GLES30.glGenVertexArrays(obj3Ds.size(), vao, 0);
        for (int i=0 ;i < obj3Ds.size();i++) {
            objVao.put(vao[i], obj3Ds.get(i));
            initVAO(vao[i],obj3Ds.get(i));
        }
    }

    private void initLocation() {
        LOCATION_VETEX = GLES30.glGetAttribLocation(programId, "aPos");
        LOCATION_NORMAL = GLES30.glGetAttribLocation(programId, "aNormal");
        LOCATION_TEXTURE = GLES30.glGetAttribLocation(programId, "aTexCoords");

        LOCATION_MAT_MODEL = GLES30.glGetUniformLocation(programId, "model");
        LOCATION_MAT_VIEW = GLES30.glGetUniformLocation(programId, "view");
        LOCATION_MAT_PROJ = GLES30.glGetUniformLocation(programId, "projection");
        LOCATION_MAT_NORMAL = GLES30.glGetUniformLocation(programId, "normal_matrix");

        LOCATION_MTL_KA = GLES30.glGetUniformLocation(programId, "material.ambient");
        LOCATION_MTL_KD = GLES30.glGetUniformLocation(programId, "material.diffuse");
        LOCATION_MTL_KS = GLES30.glGetUniformLocation(programId, "material.specular");
        LOCATION_MTL_NS = GLES30.glGetUniformLocation(programId, "material.shininess");

        LOCATION_LIGHT_DIR = GLES30.glGetUniformLocation(programId, "dirLight.direction");
        LOCATION_LIGHT_KA = GLES30.glGetUniformLocation(programId, "dirLight.ambient");
        LOCATION_LIGHT_KD = GLES30.glGetUniformLocation(programId, "dirLight.diffuse");
        LOCATION_LIGHT_KS = GLES30.glGetUniformLocation(programId, "dirLight.specular");

        LOCATION_EYE_POS = GLES30.glGetUniformLocation(programId, "viewPos");

    }

    @Override
    public void onSurfaceChange(int width, int height) {
        Matrix.invertM(tempmatrix, 0, modelMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, tempmatrix, 0);

    }

    @Override
    public void onDraw() {
        GLES30.glUseProgram(programId);
        //设置平行光源方向
        GLES30.glUniform3fv(LOCATION_LIGHT_DIR, 1, light, 0);
        GLES30.glUniform3fv(LOCATION_LIGHT_KA, 1, light, 3);
        GLES30.glUniform3fv(LOCATION_LIGHT_KD, 1, light, 6);
        GLES30.glUniform3fv(LOCATION_LIGHT_KS, 1, light, 9);

        //设置眼睛位置 用于计算镜面反射
        GLES30.glUniform3fv(LOCATION_EYE_POS, 1, eye, 0);

        //设置 模型矩阵/视图矩阵/投影矩阵/法向量变换矩阵
        GLES30.glUniformMatrix4fv(LOCATION_MAT_MODEL, 1, false, modelMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_MAT_VIEW, 1, false, mVMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_MAT_PROJ, 1, false, mProjMatrix, 0);
        GLES30.glUniformMatrix4fv(LOCATION_MAT_NORMAL, 1, false, normalMatrix, 0);

        for(int i=0;i<objVao.size();i++){
            int key=objVao.keyAt(i);
            Obj3D obj3D=objVao.get(key);
            //设置材质 环境光/漫反射/镜面反射/锋锐值
            GLES30.glUniform3fv(LOCATION_MTL_KA, 1, obj3D.mtl.Ka, 0);
            GLES30.glUniform3fv(LOCATION_MTL_KD, 1, obj3D.mtl.Kd, 0);
            GLES30.glUniform3fv(LOCATION_MTL_KS, 1, obj3D.mtl.Ks, 0);
            GLES30.glUniform1f(LOCATION_MTL_NS, obj3D.mtl.Ns);

            drawWithVAO(key);
        }

//        for (Obj3D obj3D : obj3Ds) {
//            //设置材质 环境光/漫反射/镜面反射/锋锐值
//            GLES30.glUniform3fv(LOCATION_MTL_KA, 1, obj3D.mtl.Ka, 0);
//            GLES30.glUniform3fv(LOCATION_MTL_KD, 1, obj3D.mtl.Kd, 0);
//            GLES30.glUniform3fv(LOCATION_MTL_KS, 1, obj3D.mtl.Ks, 0);
//            GLES30.glUniform1f(LOCATION_MTL_NS, obj3D.mtl.Ns);
//
//            drawVertex(obj3D);
//        }
    }


    private void drawVertex(Obj3D obj3D) {
        //设置 顶点/纹理/法向量
        GLES30.glEnableVertexAttribArray(LOCATION_VETEX);
        GLES30.glVertexAttribPointer(LOCATION_VETEX, 3, GLES30.GL_FLOAT, false, 0, obj3D.vert);
        GLES30.glEnableVertexAttribArray(LOCATION_NORMAL);
        GLES30.glVertexAttribPointer(LOCATION_NORMAL, 3, GLES30.GL_FLOAT, false, 0, obj3D.vertNorl);
        if (obj3D.vertTexture != null) {
            GLES30.glEnableVertexAttribArray(LOCATION_TEXTURE);
            GLES30.glVertexAttribPointer(LOCATION_TEXTURE, 2, GLES30.GL_FLOAT, false, 0, obj3D.vertTexture);
        }
        //绘制顶点
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, obj3D.vertCount);

    }

    private void initVAO(int vao, Obj3D obj3D) {
        int[] vbo = new int[3];
        GLES30.glGenBuffers(3, vbo, 0);

        GLES30.glBindVertexArray(vao);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, obj3D.vert.capacity() * 4, obj3D.vert, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(LOCATION_VETEX, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(LOCATION_VETEX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, obj3D.vertNorl.capacity() * 4, obj3D.vertNorl, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(LOCATION_NORMAL, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(LOCATION_NORMAL);

        if (obj3D.vertTexture != null) {
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[2]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, obj3D.vertTexture.capacity() * 4, obj3D.vertTexture, GLES30.GL_STATIC_DRAW);
            GLES30.glEnableVertexAttribArray(LOCATION_TEXTURE);
            GLES30.glVertexAttribPointer(LOCATION_TEXTURE, 2, GLES30.GL_FLOAT, false, 0, 0);
        }

        GLES30.glBindVertexArray(GLES30.GL_NONE);
    }

    private void drawWithVAO(int vao) {
        GLES30.glBindVertexArray(vao);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, objVao.get(vao).vertCount);
        GLES30.glBindVertexArray(GLES30.GL_NONE);
    }


}
