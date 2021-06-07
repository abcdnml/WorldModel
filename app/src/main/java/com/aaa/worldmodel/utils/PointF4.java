package com.aaa.worldmodel.utils;

public class PointF4 {
    public float x;
    public float y;
    public float z;
    public float w;

    public PointF4() {
    }

    public PointF4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public String toString() {
        return "PointF4{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}
