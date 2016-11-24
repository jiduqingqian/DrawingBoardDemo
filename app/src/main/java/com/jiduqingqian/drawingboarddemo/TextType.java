package com.jiduqingqian.drawingboarddemo;

/**
 * Created by qianhao on 16/11/1.
 */

public class TextType {
    private String text;
    private float textSize;
    private float X, Y;
    private int paintColor;
    private int paintWitdh;

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }


    public void setPaintWitdh(int paintWitdh) {
        this.paintWitdh = paintWitdh;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getPaintWitdh() {
        return paintWitdh;
    }
}
