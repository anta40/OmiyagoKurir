package com.app.omiyago.kurir.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.app.omiyago.kurir.R;
import com.app.omiyago.kurir.ui.ColorPickerDialog;

import java.io.ByteArrayOutputStream;

public class SignatureActivity extends AppCompatActivity implements ColorPickerDialog.OnColorChangedListener {

    private Paint mPaint;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    AlertDialog dialog;
    MyView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
    }

    @Override
    public void colorChanged(int color) {

    }

    public class MyView extends View {

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private int width, height;

        public MyView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            width = w;
            height = h;
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            //showDialog();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            //mPaint.setMaskFilter(null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:

                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

        public void clearDrawing(){
            setDrawingCacheEnabled(false);
            onSizeChanged(width, height, width, height);
            invalidate();

            setDrawingCacheEnabled(true);
        }
    }

    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int CLEAR_MENU_ID = Menu.FIRST + 1;
    private static final int SAVE_MENU_ID = Menu.FIRST + 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()){
            case CLEAR_MENU_ID:
                mv.clearDrawing();
                return true;

            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;

            case SAVE_MENU_ID:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, COLOR_MENU_ID, 0, "Ganti warna").setShortcut('3', 'c');
        menu.add(0, CLEAR_MENU_ID, 0, "Hapus").setShortcut('5', 'z');
        menu.add(0, SAVE_MENU_ID, 0, "Simpan").setShortcut('5', 'z');

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    private Bitmap getScaledBitmap(Bitmap b, int reqWidth, int reqHeight) {
        int bWidth = b.getWidth();
        int bHeight = b.getHeight();

        int nWidth = bWidth;
        int nHeight = bHeight;

        if(nWidth > reqWidth) {
            int ratio = bWidth / reqWidth;
            if(ratio > 0) {
                nWidth = reqWidth;
                nHeight = bHeight / ratio;
            }
        }

        if(nHeight > reqHeight) {
            int ratio = bHeight / reqHeight;
            if(ratio > 0) {
                nHeight = reqHeight;
                nWidth = bWidth / ratio;
            }
        }

        return Bitmap.createScaledBitmap(b, nWidth, nHeight, true);
    }

    private String BitmapToBase64Str(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return "data:image/png;base64,"+base64String;
    }
}
