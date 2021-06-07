package com.aaa.worldmodel.surface.map;

import android.util.SparseArray;

import com.aaa.worldmodel.surface.obj.MtlInfo;
import com.aaa.worldmodel.surface.obj.Obj3D;
import com.aaa.worldmodel.surface.obj.Path3D;
import com.aaa.worldmodel.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MapDataConverter {
    static float[] originCubeVertex = new float[]{
            -1, 1, 1,//前 左 上
            1, 1, 1,//前 右 上
            1, -1, 1,//前 右 下
            -1, -1, 1,//前 左 下
            -1, 1, -1,//后 左 上
            1, 1, -1,//后 右 上
            1, -1, -1,//后 右 下
            -1, -1, -1,//后 左 下
    };
    static float[] originCubeNormal = new float[]{
            0, 1, 0,//上
            0, -1, 0,//下
            -1, 0, 0,//左
            1, 0, 0,//右
            0, 0, 1,//前
            0, 0, -1//后
    };
    static float[] originCubeTextureCoordinate = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };
    static int[] vertexIndex = new int[]{
            0, 1, 2, 0, 2, 3,//正面两个三角形
            4, 5, 6, 4, 6, 7,//背面
            0, 4, 7, 0, 7, 3,//左侧
            1, 5, 6, 1, 6, 2, //右侧
            0, 1, 5, 0, 5, 4,//上
            3, 2, 6, 3, 6, 7
    };
    static int[] normalIndex = new int[]{
            4, 4, 4, 4, 4, 4,//正面两个三角形
            5, 5, 5, 5, 5, 5,//背面
            2, 2, 2, 2, 2, 2,//左侧
            3, 3, 3, 3, 3, 3, //右侧
            0, 0, 0, 0, 0, 0,//上
            1, 1, 1, 1, 1, 1//下
    };
    static int[] textureIndex = new int[]{
            0, 1, 2, 0, 2, 3,//正面两个三角形
            1, 0, 3, 1, 3, 2,//背面
            1, 0, 3, 1, 3, 2,//左侧
            0, 1, 2, 0, 2, 3, //右侧
            3, 2, 1, 3, 1, 0,//上
            0, 1, 2, 0, 2, 3//下
    };

    //数据转换成obj格式
    public static List<Obj3D> mapDataToObj(int width, int height, int[] data, float resolution) {

        int floorFaceCount = 0;
        int wallFaceCount = 0;
        SparseArray<boolean[]> floorFaces = new SparseArray<>();
        SparseArray<boolean[]> wallFaces = new SparseArray<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int type = data[i * width + j];
                //计算立方体6个面有无相邻
                if (type == 0) {
                    //0 墙内
                    boolean[] adjucent = new boolean[]{false, false, false, false, false, false};
                    int faceCount = isAdjucency(data, width, height, i, j, adjucent);
                    floorFaceCount += faceCount;
                    floorFaces.put(i * width + j, adjucent);
                } else if (type == 1) {
                    //1 墙
                    boolean[] adjucent = new boolean[]{false, false, false, false, false, false};
                    int faceCount = isWallAdjucency(data, width, height, i, j, adjucent);
                    wallFaceCount += faceCount;
                    wallFaces.put(i * width + j, adjucent);
                } else if (type == 2) {
                    //2 墙外
                } else {

                }
            }
        }

        List<Obj3D> obj3Ds = new ArrayList<>();
        Obj3D floor = getFloor(width, height, data, resolution, floorFaces, floorFaceCount);
        obj3Ds.add(floor);


        Obj3D wall = getWall(width, height, data, resolution, wallFaces, wallFaceCount);
        obj3Ds.add(wall);
        return obj3Ds;
    }

    public static Obj3D getFloor(int width, int height, int[] data, float resolution, SparseArray<boolean[]> faces, int count) {

//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.chat);

        MtlInfo mtlInfo = MtlInfo.newBuilder()
                .Ka(new float[]{0f, 0f, 0f})
                .Kd(new float[]{0.89300000667572f, 0.93328571319580f, 0.93999999761581f})
                .Ks(new float[]{0.09019608050585f, 0.10980392247438f, 0.13333334028721f})
                .Ke(new float[]{1f, 1f, 1f})
                .Ns(32)
//                .bitmap(bitmap)
                .illum(7)
                .build();
        FloatBuffer vertex = ByteBuffer.allocateDirect(count * 6 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer vertexNormal = ByteBuffer.allocateDirect(count * 6 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer vertexTexture = ByteBuffer.allocateDirect(count * 6 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int type = data[i * width + j];
                if (type == 0) {
                    //0 墙内
                    genCube(vertex, vertexTexture, vertexNormal, j, i, width, height, resolution, faces.get(i * width + j));
                }
            }
        }
        LogUtils.i("floor vertex size : " + vertex.toString());
        //直接画宽高大小的地板
//        genFloor(vertex, vertexTexture, vertexNormal, width, height, resolution);

        vertex.flip();
        vertexNormal.flip();
        vertexTexture.flip();
        LogUtils.i(vertex.toString());
        Obj3D obj3D = Obj3D.newBuilder()
                .mtl(mtlInfo)
                .vert(vertex)
                .vertNorl(vertexNormal)
                .vertTexture(vertexTexture)
                .vertCount(vertex.limit() / 3)
                .build();
        return obj3D;
    }

    public static Obj3D getWall(int width, int height, int[] data, float resolution, SparseArray<boolean[]> faces, int count) {
//        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.chat);

        MtlInfo mtlInfo = MtlInfo.newBuilder()
                .Ka(new float[]{0.5f, 0.5f, 0.5f})
                .Kd(new float[]{1f, 1f, 1f})
                .Ks(new float[]{1f, 1f, 1f})
                .Ke(new float[]{1f, 1f, 1f})
                .Ns(100)
//                .bitmap(bitmap)
                .illum(7)
                .build();
        FloatBuffer vertex = ByteBuffer.allocateDirect(count * 6 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer vertexNormal = ByteBuffer.allocateDirect(count * 6 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer vertexTexture = ByteBuffer.allocateDirect(count * 6 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int type = data[i * width + j];
                //计算立方体6个面有无相邻
                if (type == 1) {
                    //1 墙
                    genWall(vertex, vertexTexture, vertexNormal, j, i, width, height, resolution, faces.get(i * width + j));
                }
            }
        }
        LogUtils.i("wall vertex size : " + vertex.toString());

        vertex.flip();
        vertexNormal.flip();
        vertexTexture.flip();
        LogUtils.i(vertex.toString());
        Obj3D obj3D = Obj3D.newBuilder()
                .mtl(mtlInfo)
                .vert(vertex)
                .vertNorl(vertexNormal)
                .vertTexture(vertexTexture)
                .vertCount(vertex.limit() / 3)
                .build();
        return obj3D;
    }

    public static void genCube(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, int x, int z, int width, int height, float resolution, boolean[] adjucent) {
        //resolution 现在默认是0.05  也就是地板方块长宽高0.1m
        float offsetX = x * resolution - width * resolution / 2;
        float offsetY = resolution / 2;
        float offsetZ = z * resolution - height * resolution / 2;
        float rectX = resolution;
        float rectY = resolution;
        float rectZ = resolution;
        addCuboidVertex(v, vt, vn, rectX, rectY, rectZ, offsetX, offsetY, offsetZ, adjucent);
    }

    public static void genWall(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, int x, int z, int width, int height, float resolution, boolean[] adjucent) {
        //resolution 现在默认是0.05  也就是墙方块长宽0.1m  高1.5m
        float offsetX = x * resolution - width * resolution / 2;
        float offsetY = 15 * resolution - resolution / 2;
        float offsetZ = z * resolution - height * resolution / 2;
        float rectX = resolution;
        float rectY = 15 * resolution;
        float rectZ = resolution;
        addCuboidVertex(v, vt, vn, rectX, rectY, rectZ, offsetX, offsetY, offsetZ, adjucent);

    }

    /**
     * 计算每个方块有无相邻, 结果存在boolean数组中
     * 顺序为 前 后 左 右 上 下
     * 有相邻 为true  不用画这个面
     * 无相邻 为false
     */
    public static int isAdjucency(int[] mapData, int w, int h, int i, int j, boolean[] adjucent) {

        int faceCount = 6; //总共6个面

        int positionFront = (i + 1) * w + j;
        int positiBack = (i - 1) * w + j;
        int positionLeft = i * w + j - 1;
        int positionRight = i * w + j + 1;
        //前面有格子
        if (i + 1 < h && mapData[positionFront] < 2) {
            adjucent[0] = true;
            faceCount--; //有相邻的面不画
        }

        //后面有格子
        if (i - 1 >= 0 && mapData[positiBack] < 2) {
            adjucent[1] = true;
            faceCount--;
        }

        //左面有格子
        if (j - 1 >= 0 && mapData[positionLeft] < 2) {
            adjucent[2] = true;
            faceCount--;
        }

        //右面有格子
        if (j + 1 < w && mapData[positionRight] < 2) {
            adjucent[3] = true;
            faceCount--;
        }

        //上下两个面暂时都画
        adjucent[4] = false;
        adjucent[5] = false;
        return faceCount;
    }

    public static int isWallAdjucency(int[] mapData, int w, int h, int i, int j, boolean[] adjucent) {

        int faceCount = 6;

        int positionFront = (i + 1) * w + j;
        int positiBack = (i - 1) * w + j;
        int positionLeft = i * w + j - 1;
        int positionRight = i * w + j + 1;
        //前面有格子
        if (i + 1 < h && mapData[positionFront] == 1) {
            adjucent[0] = true;
            faceCount--;
        }

        //后面有格子
        if (i - 1 >= 0 && mapData[positiBack] == 1) {
            adjucent[1] = true;
            faceCount--;
        }

        //左面有格子
        if (j - 1 >= 0 && mapData[positionLeft] == 1) {
            adjucent[2] = true;
            faceCount--;
        }

        //右面有格子
        if (j + 1 < w && mapData[positionRight] == 1) {
            adjucent[3] = true;
            faceCount--;
        }

        //上下两个面暂时都画
        adjucent[4] = false;
        adjucent[5] = false;
        return faceCount;
    }

    public static void addCuboidVertex(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, float rectX, float rectY, float rectZ, float offsetX, float offsetY, float offsetZ, boolean[] adjucent) {
        //重新设置矩形大小和偏移
        float[] vertex = new float[24];
        for (int i = 0; i < vertex.length; i++) {
            if (i % 3 == 0) {
                vertex[i] = originCubeVertex[i] * rectX + offsetX;
            } else if (i % 3 == 1) {
                vertex[i] = originCubeVertex[i] * rectY + offsetY;
            } else if (i % 3 == 2) {
                vertex[i] = originCubeVertex[i] * rectZ + offsetZ;
            }
        }

        for (int i = 0; i < adjucent.length; i++) {
            //根据adjucent 判断需要添加几个面 有相邻的面不用添加
            //每个面六个点 每个点有 坐标xyz 法向量xyz 纹理坐标xy
            if (!adjucent[i]) {
                for (int j = 0; j < 6; j++) {
                    int k = i * 6 + j;
                    v.put(vertex[vertexIndex[k] * 3]);
                    v.put(vertex[vertexIndex[k] * 3 + 1]);
                    v.put(vertex[vertexIndex[k] * 3 + 2]);

                    vn.put(originCubeNormal[normalIndex[k] * 3]);
                    vn.put(originCubeNormal[normalIndex[k] * 3 + 1]);
                    vn.put(originCubeNormal[normalIndex[k] * 3 + 2]);

                    vt.put(originCubeTextureCoordinate[textureIndex[k] * 2]);
                    vt.put(originCubeTextureCoordinate[textureIndex[k] * 2 + 1]);

                }
            }
        }
    }

    public static Path3D convertPathData(float width, float height, float resolution, float x_min, float y_min, List<Integer> path) {

        FloatBuffer vertex = ByteBuffer.allocateDirect(path.size() / 2 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        xx[i] = int(path_xx[i + 4] / 5.0 - x0 * 20);
//        yy[i] = int(path_yy[i + 4] / 5.0 - y0 * 20);
//        xxx[i] = (width - yy[i]);
//        yyy[i] = (height - xx[i]);

        for (int i = 0; i < path.size() / 2; i++) {
            float x = width - (path.get(i * 2 + 1) / 5 - y_min * 20);
            float z = height - (path.get(i * 2) / 5 - x_min * 20);

            x = x * resolution - width * resolution / 2;
            z = z * resolution - height * resolution / 2;

            float y = 0.15f;
            vertex.put(x);
            vertex.put(y);
            vertex.put(z);
        }
        vertex.flip();

        Path3D path3D = new Path3D();
        path3D.setVert(vertex);

        return path3D;
    }

    /**
     * 只用一个长方体绘制地面, 不考虑清扫
     */
    public void genFloor(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, int width, int height, float resolution) {
        float offsetX = -width * resolution / 2;
        float offsetY = -resolution / 2;
        float offsetZ = -height * resolution / 2;
        float rectX = width * resolution;
        float rectY = resolution;
        float rectZ = height * resolution;
        addCuboidVertex(v, vt, vn, rectX, rectY, rectZ, offsetX, offsetY, offsetZ);
    }

    public void addCuboidVertex(FloatBuffer v, FloatBuffer vt, FloatBuffer vn, float rectX, float rectY, float rectZ, float offsetX, float offsetY, float offsetZ) {
        float[] vertex = new float[]{
                -rectX + offsetX, rectY + offsetY, rectZ + offsetZ,//前 左 上
                rectX + offsetX, rectY + offsetY, rectZ + offsetZ,//前 右 上
                rectX + offsetX, -rectY + offsetY, rectZ + offsetZ,//前 右 下
                -rectX + offsetX, -rectY + offsetY, rectZ + offsetZ,//前 左 下
                -rectX + offsetX, rectY + offsetY, -rectZ + offsetZ,//后 左 上
                rectX + offsetX, rectY + offsetY, -rectZ + offsetZ,//后 右 上
                rectX + offsetX, -rectY + offsetY, -rectZ + offsetZ,//后 右 下
                -rectX + offsetX, -rectY + offsetY, -rectZ + offsetZ,//后 左 下
        };

        int[] index = new int[]{
                0, 1, 2, 0, 2, 3,//正面两个三角形
                4, 5, 6, 4, 6, 7,//背面
                0, 4, 7, 0, 7, 3,//左侧
                1, 5, 6, 1, 6, 2, //右侧
                0, 1, 5, 0, 5, 4,//上
                3, 2, 6, 3, 6, 7
        };
        float[] normal = new float[]{
                0, 1, 0,//上
                0, -1, 0,//下
                -1, 0, 0,//左
                1, 0, 0,//右
                0, 0, 1,//前
                0, 0, -1//后
        };
        int[] normalIndex = new int[]{
                4, 4, 4, 4, 4, 4,//正面两个三角形
                5, 5, 5, 5, 5, 5,//背面
                2, 2, 2, 2, 2, 2,//左侧
                3, 3, 3, 3, 3, 3, //右侧
                0, 0, 0, 0, 0, 0,//上
                1, 1, 1, 1, 1, 1//下
        };

        float[] texture = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };

        int[] textureIndex = new int[]{
                0, 1, 2, 0, 2, 3,//正面两个三角形
                1, 0, 3, 1, 3, 2,//背面
                1, 0, 3, 1, 3, 2,//左侧
                0, 1, 2, 0, 2, 3, //右侧
                3, 2, 1, 3, 1, 0,//上
                0, 1, 2, 0, 2, 3//下
        };

        for (int i = 0; i < index.length; i++) {
            v.put(vertex[index[i] * 3]);
            v.put(vertex[index[i] * 3 + 1]);
            v.put(vertex[index[i] * 3 + 2]);

            vn.put(normal[normalIndex[i] * 3]);
            vn.put(normal[normalIndex[i] * 3 + 1]);
            vn.put(normal[normalIndex[i] * 3 + 2]);

            vt.put(texture[textureIndex[i] * 2]);
            vt.put(texture[textureIndex[i] * 2 + 1]);
        }
    }


}
