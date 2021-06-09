/*
 *
 * FastDrawerHelper.java
 *
 * Created by Wuwang on 2016/11/17
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.aaa.worldmodel.utils;

import android.opengl.GLES30;
import android.opengl.Matrix;

/**
 * Description:
 */
public enum MatrixUtils {
    ;
    public static final int TYPE_FITXY = 0;
    public static final int TYPE_CENTERCROP = 1;
    public static final int TYPE_CENTERINSIDE = 2;
    public static final int TYPE_FITSTART = 3;
    public static final int TYPE_FITEND = 4;
    private static final float[] _tempGluUnProjectData = new float[40];
    private static final int _temp_m = 0;
    private static final int _temp_A = 16;
    private static final int _temp_in = 32;
    private static final int _temp_out = 36;

    MatrixUtils() {

    }

    /**
     * use {@link #getMatrix} instead
     */
    @Deprecated
    public static void getShowMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int
            viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            } else {
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static void getMatrix(float[] matrix, int type, int imgWidth, int imgHeight, int viewWidth,
                                 int viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (type == TYPE_FITXY) {
                Matrix.orthoM(projection, 0, -1, 1, -1, 1, 1, 3);
                Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
                Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
            }
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            if (sWhImg > sWhView) {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 1, 1 - 2 * sWhImg / sWhView, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, -1, 1, -1, 2 * sWhImg / sWhView - 1, 1, 3);
                        break;
                }
            } else {
                switch (type) {
                    case TYPE_CENTERCROP:
                        Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
                        break;
                    case TYPE_CENTERINSIDE:
                        Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
                        break;
                    case TYPE_FITSTART:
                        Matrix.orthoM(projection, 0, -1, 2 * sWhView / sWhImg - 1, -1, 1, 1, 3);
                        break;
                    case TYPE_FITEND:
                        Matrix.orthoM(projection, 0, 1 - 2 * sWhView / sWhImg, 1, -1, 1, 1, 3);
                        break;
                }
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static void getCenterInsideMatrix(float[] matrix, int imgWidth, int imgHeight, int viewWidth, int
            viewHeight) {
        if (imgHeight > 0 && imgWidth > 0 && viewWidth > 0 && viewHeight > 0) {
            float sWhView = (float) viewWidth / viewHeight;
            float sWhImg = (float) imgWidth / imgHeight;
            float[] projection = new float[16];
            float[] camera = new float[16];
            if (sWhImg > sWhView) {
                Matrix.orthoM(projection, 0, -1, 1, -sWhImg / sWhView, sWhImg / sWhView, 1, 3);
            } else {
                Matrix.orthoM(projection, 0, -sWhView / sWhImg, sWhView / sWhImg, -1, 1, 1, 3);
            }
            Matrix.setLookAtM(camera, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
            Matrix.multiplyMM(matrix, 0, projection, 0, camera, 0);
        }
    }

    public static float[] rotate(float[] m, float angle) {
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return m;
    }

    public static float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) {
            Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        }
        return m;
    }

    public static float[] scale(float[] m, float x, float y) {
        Matrix.scaleM(m, 0, x, y, 1);
        return m;
    }

    public static float[] getOriginalMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }

    /**
     * 将物体坐标转换成世界坐标
     *
     * @param matrixLeft
     * @param vectorRight
     * @return
     */
    public static PointF4 getTranslatePoint(float[] matrixLeft, PointF3 vectorRight) {
        PointF4 p4 = GLKMatrix4MultiplyVector4(matrixLeft, new PointF4(vectorRight.x, vectorRight.y, vectorRight.z, 1.0f));
//        return PointF3MultiplyScalar(new PointF3(v4.x, v4.y, v4.z), 1.0f / v4.w);
        return new PointF4(p4.x / p4.w, p4.y / p4.w, p4.z / p4.w, 1.0f / p4.w);
    }

    public static PointF4 GLKMatrix4MultiplyVector4(float[] matrixLeft, PointF4 vector) {
        PointF4 v4 = new PointF4();
        v4.x = matrixLeft[0] * vector.x + matrixLeft[4] * vector.y + matrixLeft[8] * vector.z + matrixLeft[12] * vector.w;
        v4.y = matrixLeft[1] * vector.x + matrixLeft[5] * vector.y + matrixLeft[9] * vector.z + matrixLeft[13] * vector.w;
        v4.z = matrixLeft[2] * vector.x + matrixLeft[6] * vector.y + matrixLeft[10] * vector.z + matrixLeft[14] * vector.w;
        v4.w = matrixLeft[3] * vector.x + matrixLeft[7] * vector.y + matrixLeft[11] * vector.z + matrixLeft[15] * vector.w;
        return v4;
    }

    public static int gluUnProject(float winx, float winy, float winz,
                                   float[] model,
                                   float[] proj,
                                   int[] viewport,
                                   float[] xyz) {
        /* Normalize between -1 and 1 */
        _tempGluUnProjectData[_temp_in] = (winx - viewport[0]) * 2f / viewport[2] - 1.0f;
        _tempGluUnProjectData[_temp_in + 1] = (winy - viewport[1]) * 2f / viewport[3] - 1.0f;
        _tempGluUnProjectData[_temp_in + 2] = 2f * winz - 1.0f;
        _tempGluUnProjectData[_temp_in + 3] = 1.0f;
        /* Get the inverse */
        Matrix.multiplyMM(_tempGluUnProjectData, _temp_A, proj, 0, model, 0);
        Matrix.invertM(_tempGluUnProjectData, _temp_m, _tempGluUnProjectData, _temp_A);
        Matrix.multiplyMV(_tempGluUnProjectData, _temp_out,
                _tempGluUnProjectData, _temp_m,
                _tempGluUnProjectData, _temp_in);
        if (_tempGluUnProjectData[_temp_out + 3] == 0.0) {
            return GLES30.GL_FALSE;
        }
        xyz[0] = _tempGluUnProjectData[_temp_out] / _tempGluUnProjectData[_temp_out + 3];
        xyz[1] = _tempGluUnProjectData[_temp_out + 1] / _tempGluUnProjectData[_temp_out + 3];
        xyz[2] = _tempGluUnProjectData[_temp_out + 2] / _tempGluUnProjectData[_temp_out + 3];
        return GLES30.GL_TRUE;
    }

    public static float[] unProject(int x, int y,
                          int viewWidth, int viewHeight,
                          float[] proj,
                          float[] model)
    {
        float z = 0;
        float[] xyz=new float[3];
        int[] viewport=new int[4];
        viewport[0]=0;
        viewport[1]=0;
        viewport[2]=viewWidth;
        viewport[3]=viewHeight;
        gluUnProject(x, viewHeight -y, z, model, proj, viewport, xyz);
        return xyz;
    }


}
