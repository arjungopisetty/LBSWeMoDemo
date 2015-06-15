package com.corning.helloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by WoodRL on 5/13/2015.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.WindowManager;

public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Display mDisplay;
    private SurfaceHolder mSurfaceHolder;
    private static boolean mIsSurfaceCreated;
    private float mSurfaceWidth;
    private float mSurfaceHeight;


    private float roomScale = 0;
    private float roomDimX = 26.0f;
    private float roomDimY = 36.0f;
    private int mWidth;
    private int mHeight;
    private double xLast = 0.0;
    private double yLast = 0.0;

    public MapSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        mSurfaceHolder = getHolder();
        setFocusable(true);
        mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public MapSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mSurfaceHolder = getHolder();
        setFocusable(true);
        mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private void getViewInfo() {
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsSurfaceCreated = true;
        getViewInfo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsSurfaceCreated = false;
    }

    public void RenderSurface(double x, double y) {
        if (mIsSurfaceCreated) {
            Canvas canvas;
            canvas = null;
            try {
                canvas = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    mSurfaceWidth = canvas.getWidth();
                    mSurfaceHeight = canvas.getHeight();

                    roomScale = (mSurfaceHeight / roomDimY);
                    float scaleRoomX = (mSurfaceWidth / roomDimX);
                    float scaleRoomY = (mSurfaceHeight / roomDimY);
                    float roomWidth = (float) (roomDimX * roomScale);

                    Display disp = getDisplay();
                    Point pnt = new Point();
                    disp.getSize(pnt);

                    int sWidth = pnt.x;
                    int sHeight = pnt.y;

                    canvas.drawColor(Color.BLACK);
                    Paint p = new Paint();

                    Bitmap mFloorPlan = BitmapFactory.decodeResource(getResources(), R.drawable.corning_floor_plan);
                    Bitmap mScaledMap = null;
                    float mht = mFloorPlan.getHeight();
                    float mwt = mFloorPlan.getWidth();
                    float dht = canvas.getHeight();
                    float dwt = canvas.getWidth();

                    float mapScale = (dht / mht);

                    if (mScaledMap != null) {
                        mScaledMap.recycle();
                        mScaledMap = null;
                    }

                    /*double mapX, mapY = 0.0;

                    mapX = (x * roomScale) * -1;
                    mapY = (y * roomScale) * -1;*/

                    int newWidth = Math.round(mFloorPlan.getWidth() * (mapScale));
                    int newHeight = Math.round(mFloorPlan.getHeight() * (mapScale));
                    if (mScaledMap == null) {
                        mScaledMap = Bitmap.createScaledBitmap(mFloorPlan, newWidth, newHeight, false);
                        canvas.drawBitmap(mScaledMap, 0, 0, null);
                        // mMap.recycle();
                    } else {
                        canvas.drawBitmap(mScaledMap, 0, 0, null);
                    }

                    //x *= -1;
                    //float cx = (float) (W - x) * roomScale;
                    //float cx = (float) (roomDimX - x) * roomScale;
                    float cx = (float) x * roomScale;
                    float cy = (float) (roomDimY - y) * roomScale;
                    if (cx > mSurfaceWidth) {
                        cx = mSurfaceWidth;
                    } else if (cx < 0.0f) {
                        cx = 0.0f;
                    }

                    if (cy > mSurfaceHeight) {
                        cy = mSurfaceHeight;
                    } else if (cy < 0.0f) {
                        cy = 0.0f;
                    }

                    p.setColor(Color.WHITE);
                    canvas.drawCircle(cx, cy, (float)(0.8 * roomScale), p);
                    p.setColor(Color.BLACK);
                    canvas.drawCircle(cx, cy, (float)(0.6 * roomScale), p);
                    p.setColor(Color.CYAN);
                    canvas.drawCircle(cx, cy, (float)(0.5 * roomScale), p);
                    p.setColor(Color.GREEN);
                    canvas.drawCircle(0.0f, 0.0f, (float)(0.5 * roomScale), p);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
