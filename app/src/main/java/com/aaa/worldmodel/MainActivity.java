package com.aaa.worldmodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.aaa.worldmodel.surface.WorldRender;
import com.aaa.worldmodel.surface.WorldSurfaceView;
import com.aaa.worldmodel.surface.map.MapDataConverter;
import com.aaa.worldmodel.surface.model.Cube;
import com.aaa.worldmodel.surface.model.ImageHandle;
import com.aaa.worldmodel.surface.model.Obj3DShape;
import com.aaa.worldmodel.surface.model.Obj3DShape1;
import com.aaa.worldmodel.surface.model.ObjModel;
import com.aaa.worldmodel.surface.model.PathDrawable;
import com.aaa.worldmodel.surface.model.SkyBoxModel;
import com.aaa.worldmodel.surface.model.TestModel;
import com.aaa.worldmodel.surface.model.Texture2D;
import com.aaa.worldmodel.surface.model.Triangle;
import com.aaa.worldmodel.surface.obj.Obj3D;
import com.aaa.worldmodel.surface.obj.ObjReader;
import com.aaa.worldmodel.surface.obj.Path3D;
import com.aaa.worldmodel.utils.LogUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import static java.lang.Math.PI;

public class MainActivity extends AppCompatActivity {


    private static WorldRender worldRender;
    private WorldSurfaceView worldSurfaceView;
    private Handler handler = new Handler();
    private SeekBar sb_x;
    private SeekBar sb_y;
    private SeekBar sb_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay().getRealMetrics(metrics);
        Log.i("aaaaaaaaaa", " metrics.densityDpi " + metrics.densityDpi + "density : " + metrics.density);

        sb_x=findViewById(R.id.sb_rotateX);
        sb_x.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                worldRender.ro
            }
        });
        sb_y=findViewById(R.id.sb_rotateY);
        sb_y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_z=findViewById(R.id.sb_rotateZ);
        sb_z.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        worldSurfaceView = findViewById(R.id.sv_world);
        worldRender = worldSurfaceView.getRenderer();
//        addTriangle();
//        addCircle();
//        addCone();
//        addCube1(0,0,0);
//        addColorfulCube();
//        addColumn();
//        addGlobe();
//        addTexture2D();
//        addTexture2DEffect();
//        add3DObj();
//        addMulti3DObj();
//        test3D();
//        map();
        addSkyBox();
    }

    public void addTriangle() {
        final float UNIT_SIZE = 0.2f;
        float[] vertex = new float[]{
                0, 0, 0,
                -UNIT_SIZE, UNIT_SIZE, 0,
                0, UNIT_SIZE, 0,
                UNIT_SIZE, UNIT_SIZE, 0,
                UNIT_SIZE, 0, 0,
                UNIT_SIZE, -UNIT_SIZE, 0,
                0, -UNIT_SIZE, 0,
                -UNIT_SIZE, -UNIT_SIZE, 0,
                -UNIT_SIZE, 0, 0,
                -UNIT_SIZE, UNIT_SIZE, 0,
        };
        float[] color = new float[]{
                1, 1, 1,
                1, 0, 0,
                0, 1, 0,
                0, 0, 1,
                1, 1, 0,
                0, 1, 1,
                1, 0, 1,
                0, 0, 0,
                1, 0, 1,
                1, 0, 0,
        };

        Triangle triangle = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertex)
                .colorBuffer(color)
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(triangle);
    }

    public void addCircle() {
        int triangleCount = 100;
        final float UNIT_SIZE = 0.4f;

        FloatBuffer vertexBuffer = FloatBuffer.allocate((triangleCount + 2) * 3);
        FloatBuffer colorBuffer = FloatBuffer.allocate((triangleCount + 2) * 3);

        //???????????? ??????GL_TRIANGLE_FAN????????????
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(1f);

        //??????????????????
        for (int i = 0; i <= triangleCount; i++) {
            vertexBuffer.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(0f);

            colorBuffer.put(0f);
            colorBuffer.put(0f);
            colorBuffer.put(1f);
        }
        Triangle circle = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBuffer.array())
                .colorBuffer(colorBuffer.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(circle);

    }

    public void addCone() {
        int triangleCount = 100;
        final float UNIT_SIZE = 0.4f;
        FloatBuffer vertexBuffer = FloatBuffer.allocate((triangleCount + 2) * 3);
        FloatBuffer colorBuffer = FloatBuffer.allocate((triangleCount + 2) * 4);
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        vertexBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(1f);

        FloatBuffer vertexBufferUp = FloatBuffer.allocate((triangleCount + 2) * 3);
        FloatBuffer colorBufferUp = FloatBuffer.allocate((triangleCount + 2) * 4);
        vertexBufferUp.put(0f);
        vertexBufferUp.put(0f);
        vertexBufferUp.put(1f);
        colorBufferUp.put(0f);
        colorBufferUp.put(0f);
        colorBufferUp.put(1f);

        for (int i = 0; i <= triangleCount; i++) {
            vertexBuffer.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBuffer.put(0f);

            colorBuffer.put(0f);
            colorBuffer.put(0f);
            colorBuffer.put(1f);

            vertexBufferUp.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferUp.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferUp.put(0f);

            colorBufferUp.put(0f);
            colorBufferUp.put(1f);
            colorBufferUp.put(0f);
        }


        Triangle circle = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBuffer.array())
                .colorBuffer(colorBuffer.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();
        worldRender.addShape(circle);

        Triangle circle1 = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferUp.array())
                .colorBuffer(colorBufferUp.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        worldRender.addShape(circle1);
    }

    public void addColumn() {
        int triangleCount = 100;
        final float UNIT_SIZE = 0.4f;
        FloatBuffer vertexBufferBottom = FloatBuffer.allocate((triangleCount + 2) * 3);
        vertexBufferBottom.put(0f);
        vertexBufferBottom.put(0f);
        vertexBufferBottom.put(0f);

        FloatBuffer colorBufferBottom = FloatBuffer.allocate((triangleCount + 2) * 3);
        colorBufferBottom.put(0f);
        colorBufferBottom.put(0f);
        colorBufferBottom.put(1f);

        FloatBuffer vertexBufferTop = FloatBuffer.allocate((triangleCount + 2) * 3);
        vertexBufferTop.put(0f);
        vertexBufferTop.put(0f);
        vertexBufferTop.put(1f);

        FloatBuffer colorBufferTop = FloatBuffer.allocate((triangleCount + 2) * 3);
        colorBufferTop.put(0.9f);
        colorBufferTop.put(0.9f);
        colorBufferTop.put(0.9f);

        FloatBuffer vertexBufferBorder = FloatBuffer.allocate((triangleCount + 1) * 2 * 3);
        FloatBuffer colorBufferBorder = FloatBuffer.allocate((triangleCount + 1) * 2 * 3);

        for (int i = 0; i <= triangleCount; i++) {
            //???????????????
            vertexBufferBottom.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferBottom.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferBottom.put(0f);

            colorBufferBottom.put(0f);
            colorBufferBottom.put(0f);
            colorBufferBottom.put(1f);

            //???????????????
            vertexBufferTop.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferTop.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferTop.put(1f);

            colorBufferTop.put(0.8f);
            colorBufferTop.put(0.8f);
            colorBufferTop.put(0.8f);


            //?????????
            vertexBufferBorder.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(0);
            colorBufferBorder.put(0.8f);
            colorBufferBorder.put(0.8f);
            colorBufferBorder.put(0.8f);

            vertexBufferBorder.put(UNIT_SIZE * (float) Math.cos(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(UNIT_SIZE * (float) Math.sin(PI / 180 * i * 360 / triangleCount));
            vertexBufferBorder.put(1);
            colorBufferBorder.put(0.9f);
            colorBufferBorder.put(0.9f);
            colorBufferBorder.put(0.9f);
        }
        Triangle circleDown = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferBottom.array())
                .colorBuffer(colorBufferBottom.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        Triangle circleUp = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferTop.array())
                .colorBuffer(colorBufferTop.array())
                .drawType(GLES30.GL_TRIANGLE_FAN)
                .build();

        Triangle border = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBufferBorder.array())
                .colorBuffer(colorBufferBorder.array())
                .drawType(GLES30.GL_TRIANGLE_STRIP)
                .build();

        worldRender.addShape(circleUp);
        worldRender.addShape(circleDown);
        worldRender.addShape(border);

    }

    public void addGlobe() {

        int triangleCount = 20;
        final float UNIT_SIZE = 0.4f;
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect((triangleCount + 1) * (triangleCount + 1) * 2 * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        FloatBuffer colorBuffer = ByteBuffer.allocateDirect((triangleCount + 1) * (triangleCount + 1) * 2 * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (int i = -90; i <= 90; i += 180 / triangleCount) {
            float z1 = UNIT_SIZE * (float) Math.sin(PI / 180 * i);
            float r1 = UNIT_SIZE * (float) Math.cos(PI / 180 * i);

            float z2 = UNIT_SIZE * (float) Math.sin(PI / 180 * (i + 180f / triangleCount));
            float r2 = UNIT_SIZE * (float) Math.cos(PI / 180 * (i + 180f / triangleCount));

            for (int j = 0; j <= 360; j += 360 / triangleCount) {
                float x1 = r1 * (float) Math.cos(PI / 180 * j);
                float y1 = r1 * (float) Math.sin(PI / 180 * j);

                float x2 = r2 * (float) Math.cos(PI / 180 * j);
                float y2 = r2 * (float) Math.sin(PI / 180 * j);

                Log.i("x1 y1 z1 ", " (" + x1 + "," + y1 + "," + z1 + ")");
                vertexBuffer.put(x1);
                vertexBuffer.put(y1);
                vertexBuffer.put(z1);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);

                Log.i("x2 y2 z2 ", " (" + x2 + "," + y2 + "," + z2 + ")");
                vertexBuffer.put(x2);
                vertexBuffer.put(y2);
                vertexBuffer.put(z2);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);
                colorBuffer.put((i + 90) / 180f);
            }
        }
        vertexBuffer.flip();
        colorBuffer.flip();
        Triangle border = Triangle.newBuilder()
                .context(this)
                .vertexBuffer(vertexBuffer)
                .colorBuffer(colorBuffer)
                .drawType(GLES30.GL_TRIANGLE_STRIP)
                .build();

        worldRender.addShape(border);


    }

    public void addCube(float offsetX, float offsetY, float offsetZ) {
        final float UNIT_SIZE = 0.1f;
        float[] cubeVertex = new float[]{
                -UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                -UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                -UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
                -UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
        };
        int[] index = new int[]{
                0, 1, 2, 0, 2, 3,//?????????????????????
                4, 5, 6, 4, 6, 7,//??????
                0, 4, 7, 0, 7, 3,//??????
                1, 5, 6, 1, 6, 2, //??????
                0, 1, 5, 0, 4, 5,//???
                3, 2, 6, 3, 7, 6
        };
        float[] color = new float[]{
                0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                0, 0, 0,
                1, 1, 0,
                0, 1, 1,
                1, 0, 1,
                0, 0, 0
        };

        Cube cube = new Cube(this, cubeVertex, index, color);

        worldRender.addShape(cube);
    }

    public void addCube1(float offsetX, float offsetY, float offsetZ) {
        final float UNIT_SIZE = 0.5f;
        float[] cubeVertex = new float[]{
                -UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                -UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, UNIT_SIZE + offsetZ,//??? ??? ???
                -UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
                UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
                -UNIT_SIZE + offsetX, -UNIT_SIZE + offsetY, -UNIT_SIZE + offsetZ,//??? ??? ???
        };
        int[] index = new int[]{
                0, 1, 2, 0, 2, 3,//?????????????????????
                4, 5, 6, 4, 6, 7,//??????
                0, 4, 7, 0, 7, 3,//??????
                1, 5, 6, 1, 6, 2, //??????
                0, 1, 5, 0, 4, 5,//???
                3, 2, 6, 3, 7, 6
        };
        float[] color = new float[]{
                1, 0, 0,
                0, 1, 0,
                0, 0, 1,
                0, 0, 0,
                1, 1, 1,
                1, 1, 0,
                0, 1, 1,
                1, 0, 1
        };

        TestModel cube = new TestModel(this, cubeVertex, index, color);

        worldRender.addShape(cube);
    }

    public  void addSkyBox(){
        SkyBoxModel skyBoxModel=new SkyBoxModel(this);
        worldRender.addShape(skyBoxModel);
    }

    public void addColorfulCube() {
        final float UNIT_SIZE = 0.8f;
        //????????????????????????3?????? ??????????????????????????? ?????????????????????????????? ??????????????????
        float[] cubeVertex = new float[]{
                //?????????0
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//0??? ??? ??? 1
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//1??? ??? ??? 5
                -UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//2??? ??? ??? 3

                //?????????1
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//3??? ??? ??? 1
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//4??? ??? ??? 4
                UNIT_SIZE, UNIT_SIZE, UNIT_SIZE,//5??? ??? ??? 5

                //?????????2
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//6??? ??? ??? 1
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//7??? ??? ??? 4
                UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//8??? ??? ??? 6

                //?????????3
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//9??? ??? ??? 1
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//10??? ??? ??? 3
                -UNIT_SIZE, -UNIT_SIZE, UNIT_SIZE,//11??? ??? ??? 6

                //?????????4
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//12??? ??? ??? 2
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//13??? ??? ??? 3
                -UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//14??? ??? ??? 5

                //?????????5
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//15??? ??? ??? 2
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//16??? ??? ??? 4
                UNIT_SIZE, UNIT_SIZE, -UNIT_SIZE,//17??? ??? ??? 5

                //?????????6
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//18??? ??? ??? 2
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//19??? ??? ??? 4
                UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//20??? ??? ??? 6

                //?????????7
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//21??? ??? ??? 2
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//22??? ??? ??? 3
                -UNIT_SIZE, -UNIT_SIZE, -UNIT_SIZE,//23??? ??? ??? 6


        };

        int[] index = new int[]{
                0, 3, 6, 0, 6, 9,//?????? ??????1
                12, 15, 18, 12, 18, 21,//??????2
                2, 13, 22, 2, 22, 10,//??????3
                4, 16, 19, 4, 19, 7, //??????4
                1, 5, 17, 1, 14, 17,//???5
                11, 8, 20, 11, 23, 20 //6
        };

        /*
        1:100
        2:010
        3:001
        4:110
        5:011
        6:101
         */
        float[] color = new float[]{
                1, 0, 0,
                0, 1, 1,
                0, 0, 1,

                1, 0, 0,
                1, 1, 0,
                0, 1, 1,

                1, 0, 0,
                1, 1, 0,
                1, 0, 1,

                1, 0, 0,
                0, 0, 1,
                1, 0, 1,

                0, 1, 0,
                0, 0, 1,
                0, 1, 1,

                0, 1, 0,
                1, 1, 0,
                0, 1, 1,

                0, 1, 0,
                1, 1, 0,
                1, 0, 1,

                0, 1, 0,
                0, 0, 1,
                1, 0, 1
        };

        Cube cube = new Cube(this, cubeVertex, index, color);

        worldRender.addShape(cube);
    }

    public void addTexture2D() {
        //????????????
        float[] sPos = {
                -1.0f, 1.0f, 0f,    //?????????
                -1.0f, -1.0f, 0f,   //?????????
                1.0f, 1.0f, 0f,     //?????????
                1.0f, -1.0f, 0f,     //?????????
        };

        //????????????
        float[] sCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.huskar);
        Texture2D texture2D = new Texture2D(this, bitmap, sPos, sCoord);
        worldRender.addShape(texture2D);
    }

    public void addTexture2DEffect() {
        //????????????
        float[] sPos = {
                -1.0f, 1.0f,    //?????????
                -1.0f, -1.0f,   //?????????
                1.0f, 1.0f,     //?????????
                1.0f, -1.0f     //?????????
        };

        //????????????
        float[] sCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };
        FloatBuffer vertexCoordinate = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(sPos);
        vertexCoordinate.flip();
        FloatBuffer textureCoordinate = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(sCoord);
        textureCoordinate.flip();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.huskar);
        ImageHandle imageHandle = new ImageHandle(this, bitmap, vertexCoordinate, textureCoordinate, 4, new float[]{0f, 1f, 0f}, true);
        worldRender.addShape(imageHandle);
    }

    public void add3DObj() {
        Obj3DShape obj3DShape1 = new Obj3DShape(this);
        obj3DShape1.temp = true;
        Obj3DShape obj3DShape2 = new Obj3DShape(this);
        worldRender.addShape(obj3DShape1);
        worldRender.addShape(obj3DShape2);
    }

    public void addMulti3DObj() {
/*        List<Obj3D> multiObj= ObjReader.readMultiObj(this,"assets/obj/pikachu.obj");
        for(Obj3D obj3D: multiObj){
            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,0.008f);
            worldRender.addShape(obj3DShape1);
        }
        List<Obj3D> multiObj1= ObjReader.readMultiObj(this,"assets/obj/patrick.obj");
        for(Obj3D obj3D: multiObj1){
            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,1.0f);
            worldRender.addShape(obj3DShape1);
        }*/

//        List<Obj3D> multiObj2= ObjReader.readMultiObj(this,"assets/obj/?????????.obj");
//        LogUtils.i("obj size: "+multiObj2.size());
//        for(Obj3D obj3D: multiObj2){
//            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,0.008f);
//            worldRender.addShape(obj3DShape1);
//        }

//        List<Obj3D> multiObj2= ObjReader.readMultiObj(this,"assets/obj/Bigmax_White_OBJ.obj");
//        LogUtils.i("obj size: "+multiObj2.size());
//        for(Obj3D obj3D: multiObj2){
//            Obj3DShape1 obj3DShape1=new Obj3DShape1(this,obj3D,0.008f);
//            worldRender.addShape(obj3DShape1);
//        }

        List<Obj3D> multiObj2 = ObjReader.readMultiObj(this, "assets/obj/test.obj");
        LogUtils.i("obj size: " + multiObj2.size());
        for (Obj3D obj3D : multiObj2) {
            Obj3DShape1 obj3DShape1 = new Obj3DShape1(this, obj3D, 1);
            worldRender.addShape(obj3DShape1);
        }
    }

    public void test3D() {
        List<Obj3D> multiObj2 = ObjReader.readMultiObj(this, "assets/obj/3d??????.obj");
        float modelStride = 200;  //??????????????????  ?????????????????? ????????????0.5 ????????????????????????0.5
        LogUtils.i("obj size : " + multiObj2.size());
        ObjModel objModel = new ObjModel(this, multiObj2);
        objModel.setOffset(MapDataConverter.convertOffset(0), MapDataConverter.convertOffset(0) + 0.2f, 0);
        objModel.setRotate(0, 0, 0);
        objModel.setScale(1f);
        worldRender.addShape(objModel);
/*
        multiObj2 = ObjReader.readMultiObj(this, "assets/obj/???????????????.obj");
        modelStride=3000;  //??????????????????  ?????????????????? ????????????0.5 ????????????????????????0.5
        objModel = new ObjModel(this, multiObj2);
        objModel.setOffset(MapDataConverter.convertOffset(2),MapDataConverter.convertOffset(2)+0.2f,0);
        objModel.setRotate(0,0,0);
        objModel.setScale(MapDataConverter.calculateModelScale(1f,modelStride));
        worldRender.addShape(objModel);*/

    }

    public void map() {
        Gson gson = new Gson();
        LDMapBean ldMapBean = gson.fromJson(readAssetString("test/map.json"), LDMapBean.class);
        int[] mapdata = new int[ldMapBean.baseMapData.length()];
        for (int i = 0; i < ldMapBean.baseMapData.length(); i++) {
            mapdata[i] = ldMapBean.baseMapData.charAt(i) - '0';
        }
        Path3D path3D = MapDataConverter.convertPathData(ldMapBean.width, ldMapBean.height, ldMapBean.resolution, ldMapBean.x_min, ldMapBean.y_min, ldMapBean.path);
        PathDrawable pathDrawable = new PathDrawable(this, path3D);
        worldRender.addShape(pathDrawable);

        List<Obj3D> obj3D = MapDataConverter.mapDataToObj(ldMapBean.width, ldMapBean.height, mapdata, ldMapBean.resolution);
        ObjModel map = new ObjModel(this, obj3D);
        worldRender.addShape(map);


    }

    public String readAssetString(String path) {
        String tmp = null;
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(path)));
            while ((tmp = br.readLine()) != null) {
                buffer.append(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tmp = buffer.toString();
        LogUtils.ls(tmp);
        return tmp;
    }

    float distance=0.3f;
    public void moveUp(View view) {
        worldRender.move(0,distance,0);
    }

    public void moveLeft(View view) {
        worldRender.move(-distance,0,0);
    }

    public void moveFront(View view) {
        worldRender.move(0,0,-distance);
    }

    public void moveBack(View view) {
        worldRender.move(0,0,distance);
    }

    public void moveRight(View view) {
        worldRender.move(distance,0,0);
    }

    public void moveDown(View view) {
        worldRender.move(0,-distance,0);
    }
    public void rotateLeft(View view) {
        worldRender.rotateSelf(0,10,0);
    }
    public void rotateRight(View view) {
        worldRender.rotateSelf(0,-10,0);
    }

    public void rotateDown(View view) {
        worldRender.rotateSelf(-10,0,0);
    }

    public void rotateUp(View view) {
        worldRender.rotateSelf(10,0,0);
    }


}
