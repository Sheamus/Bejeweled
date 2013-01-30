package com.bask.appopengl;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameView extends SurfaceView  
{
	private GameThread mThread;
	 
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint;
    private float canvasSize;
    private final int horizontalCountOfCells = 6;
    private final int verticalCountOfCells = 8;
    boolean isSmallCells = false;
    boolean isBejeweled = true;
    int sizeX, sizeY, dx, dy;
    
    private SoundManager soundManager;
    
    private boolean running = false;
    
    public class GameThread extends Thread
    {
        private GameView view;	 
        
        public GameThread(GameView view) 
        {
              this.view = view;
        }

        public void setRunning(boolean run) 
        {
              running = run;
        }

        public void run()
        {
            while (running)
            {
                Canvas canvas = null;
                try
                {
                    canvas = view.getHolder().lockCanvas();
                    synchronized (view.getHolder())
                    {
                        onDraw(canvas);
                    }
                }
                catch (Exception e) { }
                finally
                {
                    if (canvas != null)
                    {
                    	view.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
        
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mThread = new GameThread(this);
        
        Point startPoint = new Point();

        canvasSize=(int)convertDpToPixel(300, context);

        mBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background00));
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mCanvas = new Canvas(mBitmap);
         
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAlpha(30);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAlpha(255);

        sizeX = (int)(canvasSize / horizontalCountOfCells);
        sizeY = (int)(canvasSize / verticalCountOfCells);
        
        if(isSmallCells)
        {
        	sizeX = 45;
        	sizeY = 45;
        }
        else{
        	sizeX = 50;
        	sizeY = 50;
        }
        
        Random random = new Random();
        
        for(int x=0;x< horizontalCountOfCells;x++)
        	for(int y=0;y< verticalCountOfCells;y++)
        	{
        		if(isBejeweled)
        		{
	        		if((x+y)%2==0){
	        			paint.setColor(0xff000000);
	        			paint.setAlpha(50);
	        		}
	        		else{
	        			paint.setColor(0xffffffff);
	        			paint.setAlpha(10);
	        		}
        		}
        		
        		dy = 0;
        		dx = sizeX;
        		
        		if(!isBejeweled)
        		{	
        			dx = (int)(sizeX * Math.cos(30*Math.PI/180));
        			if(x%2==0) dy = (int)(sizeY/2);
        		}
        		
                Rect r = new Rect( 
                		x*dx + 2, 
                		y*sizeY + 25 + dy, 
                		(x+1)*dx + 2, 
                		(y+1)*sizeY + 25 + dy);

                int n = random.nextInt(7);
                
                Bitmap bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item7));
                
                if(n<1) bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item1));
                else if(n<2) bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item2));
                else if(n<3) bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item3));
                else if(n<4) bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item4));
                else if(n<5) bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item5));
                else if(n<6) bmp = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item6));
                
        		mCanvas.drawRect(r, paint);
        		mCanvas.drawBitmap(bmp, r.left+2, r.top+2, paint2);

            	Paint paint3 = new Paint();
                paint3.setAntiAlias(true);
                paint3.setDither(true);
                paint3.setStyle(Paint.Style.FILL);
                paint3.setAlpha(255);
                paint3.setColor(0xffff0000);

                mCanvas.drawPoint(r.left+5, r.top+5, paint3);
        	}
        
        /*Рисуем все наши объекты и все все все*/
        getHolder().addCallback(new SurfaceHolder.Callback() 
        {
      	  	 /*** Уничтожение области рисования */
               public void surfaceDestroyed(SurfaceHolder holder) 
               {
            	   Log.i("GameView", "surfaceDestroyed");
            	   boolean retry = true;
            	    mThread.setRunning(false);
            	    while (retry)
            	    {
            	        try
            	        {
            	            // ожидание завершение потока
            	            mThread.join();
            	            retry = false;
            	        }
            	        catch (InterruptedException e) { }
            	    }
               }

               /** Создание области рисования */
               public void surfaceCreated(SurfaceHolder holder) 
               {
            	   mThread.setRunning(true);
            	   mThread.start();
               }

               /** Изменение области рисования */
               public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
               {
               }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

    public float convertDpToPixel(float dp,Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi/160f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

    	Paint paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setDither(true);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setAlpha(255);
        paint3.setColor(0xffff0000);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setAlpha(255);
        paint2.setColor(0xffffffff);

        int cellX = (int)((event.getX()-5) / dx); 
        int cellY = (int)((event.getY()- 25) / sizeY); 

        int dy = 0;
        
        if(!isBejeweled)
        if(cellX%2==0) dy = (int)(sizeY/2);

        Rect r = new Rect( 
        		cellX*dx + 5, 
        		cellY*sizeY + 25 + dy, 
        		(cellX+1)*dx + 5, 
        		(cellY+1)*sizeY + 25 + dy);
        
        mCanvas.drawRect(r, paint2);
        mCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        invalidate();
        return true;
    }
    
    public boolean onTouch(View view, MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_DOWN)
 		{
    		SoundManager.playSound(1, 1);   		
 		}
 	   
 	   if(event.getAction() == MotionEvent.ACTION_MOVE)
 		{
 		}
        return true;
    }
}