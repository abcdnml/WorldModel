package com.aaa.worldmodel.utils;

public class PointF3 {
    float x;
    float y;
    float z;

    public PointF3() {
    }

    public PointF3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "PointF3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
