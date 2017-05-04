package com.example.czarny.czarnylab4rysowanie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.renderscript.Matrix2f;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by student on 21.04.17.
 */
public class PowierzchniaRysunku extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mPojemnik;
    private Thread mWatekRysujacy;
    private boolean mWatekPracuje = false;
    private boolean mWatekCzeka=false;
    private Object mBlokada=new Object();

    private Bitmap mBitmapa = null;
    private Canvas mKanwa=null;
    private Path mSciezka=null;
    private Paint mFarba=new Paint();

    public PowierzchniaRysunku(Context context, AttributeSet attrs){
        super(context, attrs);

        //Pojemnik powierzchi
        mPojemnik=getHolder();
        mPojemnik.addCallback(this);

        //ustawienia początkowe farby
        mFarba.setColor(Color.RED);
        mFarba.setStyle(Paint.Style.STROKE);
        mFarba.setStrokeWidth(5);



    }

    public void czysc() { //Czyszczenie ekranu (nadpisanie go nową powierzchnią rysującą)
        mBitmapa = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mKanwa = new Canvas(mBitmapa);
        mKanwa.drawARGB(255, 255, 255, 255);
        wznowRysowanie();
        mKanwa.drawBitmap(mBitmapa, 0, 0, null);
        pauzujRysowanie();
    }

    public void wznowRysowanie(){ //Wznowienie rysowania
        mWatekRysujacy=new Thread(this);
        mWatekPracuje=true;
        mWatekCzeka=false;
        mWatekRysujacy.start();
    }

    public void pauzujRysowanie(){
        mWatekCzeka=false;
    } //Wstrzymanie rysowania

    @Override
    public boolean onTouchEvent(MotionEvent event){
        performClick();
        synchronized (mBlokada){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://dotknięcie ekranu
                    wznowRysowanie();
                    mKanwa.drawCircle(event.getX(), event.getY(), 5, mFarba);
                    mSciezka=new Path(); //nowa ścieżka rysowania
                    mSciezka.moveTo(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE: //przesunięcie palca po ekranie
                    mSciezka.lineTo(event.getX(), event.getY());
                    mKanwa.drawPath(mSciezka, mFarba);
                    break;
                case MotionEvent.ACTION_UP: //puszczenie ekranu
                    mKanwa.drawCircle(event.getX(), event.getY(), 5, mFarba);
                    pauzujRysowanie();
                    break;
            }
        }
        return true;
    }
    //obsługa dotknięcia ekranu
    public boolean performClick(){
        return super.performClick();
    }
    //wybór koloru rysowania
    public void wybierzKolor(int color) {
        mFarba.setColor(color);
    }

    @Override
    public void run() {
        while (mWatekPracuje) {
            Canvas kanwa = null;
            try {
                //sekcja krytyczna, tego pojemnika może używać tylko 1 wątek
                synchronized (mPojemnik) {
                    //czy powierzchnia prawidłowa
                    if (!mPojemnik.getSurface().isValid()) continue;
                    //zwraca kanwę do rysowania
                    kanwa = mPojemnik.lockCanvas(null);

                    //sekcja krytyczna - dostęp do rysunku na wyłączność
                    synchronized (mBlokada) {
                        if (mWatekPracuje) {
                            kanwa.drawBitmap(mBitmapa, 0,0,null);
                        }
                        if(mWatekCzeka){
                            mWatekPracuje=false;
                            mWatekCzeka=false;
                        }
                    }
                }
            } finally {
                //powierzchnia spójna gdy wyjątek
                if (kanwa != null) {
                    mPojemnik.unlockCanvasAndPost(kanwa);
                }
            }
            try {
                Thread.sleep(1000 / 25); //25 klatek na sekundę
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        czysc();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        //zatrzymuje rysowanie
        mWatekPracuje=false;
    }
}
