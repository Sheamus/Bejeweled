package com.bask.appopengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.http.impl.cookie.DateUtils;
//import com.Tutorial.Sound.SoundManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.SyncStateContract.Helpers;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;

/**
 * When you tap the screen, bubbles appear on the screen. They expand and eventually pop.
 */
public class BubblesActivity extends Activity implements Callback, OnTouchListener {
	private SurfaceView surface;
	private SurfaceHolder holder;
	private JewelModel model;
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private Paint bubblePaint;
	private Bitmap mBitmap; 

	private  SoundPool mSoundPool;
	private  HashMap mSoundPoolMap;
	private  AudioManager  mAudioManager;
	private  Context mContext;
	
	Board board = new Board();
    float canvasSize;
    int horizontalCountOfCells = 6;
    int verticalCountOfCells = 8;
    boolean isSmallCells = false;
    boolean isBejeweled = true;
    int sizeX, sizeY;
    private Bitmap[] bmp = new Bitmap[8];
    
    private int GameTime = 60000;
    
    private int Score = 0;
    private int Solutions = 0;
	private String solutionLog = "";

	private long start = 0;
	private long IdleTime = 0;
	private boolean NoMoreMoves = false;
	
    public final Object LOCK = new Object();
    
    private Point FirstJewel, SecondJewel;
    private Point helpPoint;

    public void initSounds(Context theContext) {
        mContext = theContext;
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap();
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    
    public void addSound(int index, int SoundID)
    {
        mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
    }
    
    public void playSound(int index)
    {
    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play((Integer) mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }
     
    public void playLoopedSound(int index)
    {
        float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play((Integer) mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.bubbles);
        mBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background00));
        
        bmp[1] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item1));
        bmp[2] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item2));
        bmp[3] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item3));
        bmp[4] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item4));
        bmp[5] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item5));
        bmp[6] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item6));
        bmp[7] = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item7));

        FirstJewel = null;
        SecondJewel = null;
        
        sizeX = (int)(300 / horizontalCountOfCells);
        sizeY = (int)(300 / verticalCountOfCells);
        
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
        		board.field[x][y] = random.nextInt(7)+1;
        	}
        
        model = new JewelModel(board);
        
    	surface = (SurfaceView) findViewById(R.id.bubbles_surface);
    	holder = surface.getHolder();
    	surface.getHolder().addCallback(this);
    	
    	backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.BLUE);
		backgroundPaint.setAlpha(255);

		bubblePaint = new Paint();
		bubblePaint.setColor(Color.WHITE);
		bubblePaint.setAntiAlias(true);
		
		Score = 0;
		CheckBoard();
		
		surface.setOnTouchListener(this);
		
		SoundManager.playSound(2, 1);
    }
    
	@Override
	protected void onPause() {
		model.onPause(this);		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		model.onResume(this);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		model.setSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		gameLoop = new GameLoop();
		gameLoop.start();
	}
	
	private int CheckBoard()
	{
	 	ArrayList<Point> delCells = new ArrayList<Point>(); 
		
		for(int y=8-1;y>=0;y--)
		{
		 	ArrayList<Integer> cells = new ArrayList<Integer>(); 
			int cnt = 0;
			int jIdPrev = board.field[0][y];
			cells.add(0);
			for(int x=1; x<board.SizeX; x++)
			{
				int jIdCurr = board.field[x][y];
				if(jIdCurr != 0)
					if (jIdPrev == jIdCurr)
					{
						cnt++;
						cells.add(x);
						
						if (x==board.SizeX-1)
						{
							cnt++;
							if(cells.size()>=3){
								for(int i=0;i<cells.size();i++)
									delCells.add(new Point(cells.get(i), y));
								SoundManager.playSound(8, 1);
							}
						}
					}
					else
					{
						if(cells.size()>=3){
							for(int i=0;i<cells.size();i++)
								delCells.add(new Point(cells.get(i), y));
							SoundManager.playSound(8, 1);
						}
						
						cnt = 1;
						cells.clear();
						cells.add(x);
					}
				jIdPrev = jIdCurr;
			}
		}

		for(int x=0;x<board.SizeX;x++)
		{
		 	ArrayList<Integer> cells = new ArrayList<Integer>(); 
			int cnt = 0;
			int jIdPrev = board.field[x][0];
			cells.add(0);
			for(int y=1; y<board.SizeY; y++)
			{
				int jIdCurr = board.field[x][y];
				if (jIdPrev == jIdCurr)
				{
					cnt++;
					cells.add(y);
					
					if (y==board.SizeY-1)
					{
						if(cells.size()>=3 || cnt >= 3){
							for(int i=0;i<cells.size();i++)
								delCells.add(new Point(x, cells.get(i)));
							SoundManager.playSound(8, 1);
						}
					}
				}
				else
				{
					if(cells.size()>=3){
						for(int i=0;i<cells.size();i++)
							delCells.add(new Point(x, cells.get(i)));
						SoundManager.playSound(8, 1);
					}
					
					cnt = 1;
					cells.clear();
					cells.add(y);
				}
				jIdPrev = jIdCurr;
			}
		}
		
		for(int i=0;i<delCells.size();i++)
		{
			Point p = delCells.get(i);
			deleteCell(p.x, p.y);

			Score += 50;
			GameTime += 100;
			IdleTime = 0;
		}
		
		if(!model.Fallings)
		{
			for(int i=0;i<board.SizeX;i++)
				if(board.field[i][0] == 0)
				{
					board.newItemsStack[i]++;
					int newId = (new Random()).nextInt(7)+1;
					model.addJewel(i*sizeX+5, (-board.newItemsStack[i])*sizeY+25, newId, bmp[newId]);
				}
		}
		
		return delCells.size();
	}
	
	private void draw() {
		// TODO thread safety - the SurfaceView could go away while we are drawing
		
        Canvas c = new Canvas(mBitmap);

		try {
			// NOTE: in the LunarLander they don't have any synchronization here,
			// so I guess this is OK. It will return null if the holder is not ready
			c = holder.lockCanvas();
			
			// TODO this needs to synchronize on something
			if (c != null) {
				doDraw(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}

	private void doDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();

        c.drawBitmap(mBitmap, 0, 0, backgroundPaint);

        DrawBoard(c);
        
		List<JewelModel.Jewel> jewels = model.getJewels();
		for (JewelModel.Jewel jewel : jewels) {

	        Paint paint2 = new Paint();
	        paint2.setAntiAlias(true);
	        paint2.setDither(true);
	        paint2.setStyle(Paint.Style.FILL);
	        paint2.setAlpha(255);
	        Random random = new Random();

			c.drawBitmap(jewel.bmp, jewel.x + random.nextInt(4)-2, jewel.y + random.nextInt(4)-2, paint2);
		}
	}

	private void DrawBoard(Canvas c)
	{
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(0xff000000);
		paint.setAlpha(0);

		//окрашиваем фон в красно-жёлто-зелёный в зависимости от количества возможных решений
		Paint paintBg2 = new Paint();
		paintBg2.setDither(true);
		paintBg2.setStyle(Paint.Style.FILL);

		int colR, colG, val;
		val = Solutions;
		if(Solutions>12) val=12; 
		if(val<6){
			colR = 255;
			colG = (int)(val*255/6);
		}
		else
		{
			colR = (int)((12-val)*255/6);
			colG = 255;
		}
		paintBg2.setColor(Color.argb(50, colR, colG, 0));
		Rect rbg = new Rect(2, 25-3, 318, 25+8*50+3);
    	//c.drawRect(rbg, paintBg2);
		
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setAlpha(255);

        Random random = new Random();
        
        for(int x=0;x< horizontalCountOfCells;x++)
        	for(int y=0;y< verticalCountOfCells;y++)
        	{
        		if(false && isBejeweled)
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
        		
        		if(!isBejeweled)
        		{	
        		//	dx = (int)(sizeX * Math.cos(30*Math.PI/180));
        		//	if(x%2==0) dy = (int)(sizeY/2);
        		}
        		
                int jewelId = board.field[x][y];
                
                if(jewelId>0)
                {
	                Rect r = new Rect( 
	                		x*sizeX + 5, 
	                		y*sizeY+25, 
	                		(x+1)*sizeX + 5, 
	                		y*sizeY+25+sizeY);
	
	        		c.drawRect(r, paint);

	        		int dx = 0;
	        		int dy = 0;

	        		if(((FirstJewel!=null) && (x==FirstJewel.x && y==FirstJewel.y)) || (NoMoreMoves && !model.Fallings))
	        		{
        				dx = random.nextInt(4)-2;
        				dy = random.nextInt(4)-2;
	        		}

	        		if(helpPoint!=null)
	        		{
	        			if(x==helpPoint.x && y== helpPoint.y && IdleTime>5000)
	        			{
	        				if(FirstJewel == null || (FirstJewel!=null && (helpPoint.x!=FirstJewel.x && helpPoint.y != FirstJewel.y)))
	        				{
	        					dx = 0;
	        					dy = (int)(Math.cos(GameTime*Math.PI/180)*10);
	        				}
	        			}
	        		}

	        		if(jewelId>0 && jewelId<8)
	        			c.drawBitmap(bmp[jewelId], r.left + dx, r.top + dy, paint2);
                }
        		
        	}
    	Paint paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setDither(true);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setAlpha(255);
        paint3.setColor(0xff000000);
        paint3.setTextSize(20);
        
        c.drawText("SCORE: " + Score + "  SOLUTIONS: " + Solutions, 20, 25+sizeY*8 + 10, paint3);
        c.drawText(solutionLog + " " + (int)(IdleTime/1000), 20, 25+sizeY*8 + 30, paint3);
        
    	Paint paintProgress = new Paint();
    	paintProgress.setAntiAlias(true);
    	paintProgress.setDither(true);
    	paintProgress.setStyle(Paint.Style.FILL);
    	paintProgress.setAlpha(255);
    	paintProgress.setColor(0xff0000ff);
        
    	int w = (int)(280 * GameTime / 60000);
    	Rect r2 = new Rect(20, 25+8*50+35, w+20, 25+8*50+45+5);
    	c.drawRect(r2, paintProgress);
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			model.setSize(0,0);
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}
    
	private int SolveGame()
	{
		int solutions = 0;
		//Log.i("SolveGame()", "begin");
		int[][][] mask = {
				{{0,2,0,0}, {1,0,1,0}, {0,0,0,0}, {0,0,0,0}},			//mask[0] [{{}{}{}}] [{,,}]
				{{1,0,1,0}, {0,2,0,0}, {0,0,0,0}, {0,0,0,0}},
				{{1,0,0,0}, {0,2,0,0}, {1,0,0,0}, {0,0,0,0}},
				{{0,1,0,0}, {2,0,0,0}, {0,1,0,0}, {0,0,0,0}},
				
				{{1,1,0,0}, {0,0,2,0}, {0,0,0,0}, {0,0,0,0}},
				{{0,0,2,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}},
				{{0,1,1,0}, {2,0,0,0}, {0,0,0,0}, {0,0,0,0}},
				{{2,0,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}},

				{{2,0,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}},
				{{0,2,0,0}, {1,0,0,0}, {1,0,0,0}, {0,0,0,0}},
				{{1,0,0,0}, {1,0,0,0}, {0,2,0,0}, {0,0,0,0}},
				{{0,1,0,0}, {0,1,0,0}, {2,0,0,0}, {0,0,0,0}},
				
				{{2,0,1,1}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}},
				{{1,1,0,2}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}},
				{{2,0,0,0}, {0,0,0,0}, {1,0,0,0}, {1,0,0,0}},
				{{1,0,0,0}, {1,0,0,0}, {0,0,0,0}, {2,0,0,0}}
				};
		
		helpPoint = null;
		Point hp = null;
		for(int m=0; m<16; m++)
		{
			for(int y=0;y<board.SizeY;y++)
				for(int x=0;x<board.SizeX;x++)
				{
					int sum = 0;
					boolean fail = false;
					int prevColor = -1;
					int ones = 0;
					int oks = 0;
					for(int j=0;j<4;j++)
						for(int i=0; i<4;i++)
						{
							if(mask[m][j][i] > 0)
							{
								ones++;
								int xi = x + i;
								int yj = y + j;
								if(xi>=board.SizeX || yj>=board.SizeY)
								{
									fail = true;
								}
								if(!fail)
								{
									int col = board.field[xi][yj];
									if(mask[m][j][i] == 2)
										hp = new Point(xi, yj);
									if((ones==1 || col == prevColor) && col>0)
										oks++;
									prevColor = col;
								}
							}
						}
					if(oks == ones)
					{
						//Log.i("SolveGame", x+","+y + ": oks="+oks+"; ones="+ones+"; mask="+m);
						solutions++;
						solutionLog = "Mask "+m+" in ("+x+","+y+"), col:" + prevColor;
						helpPoint = hp;
					}
				}
		}
		return solutions;
	}
	
	private class GameLoop extends Thread {
		private volatile boolean running = true;
		
		public void run() {
			boolean f = true;
			boolean playedNoMoreMoves = false;
			NoMoreMoves = false;
			
			while (running) {
				try {
					// TODO don't like this hardcoding
					TimeUnit.MILLISECONDS.sleep(1);

					GameTime -= 10;
					
					running = GameTime > 0;
					
					draw();
					model.updateJewels();
					if(!model.Fallings)
						CheckBoard();
					
					if(model.Fallings && f)
					{
						//start IdleTime;
						//0
						start = System.currentTimeMillis();
						IdleTime = 0;
					}

					if(!model.Fallings && !f)
					{
						//IdleTime continuous
						//+++
						IdleTime = System.currentTimeMillis() - start; 
					}
					
					if(!model.Fallings)
					{
						Solutions = SolveGame();
						
						if(Solutions == 0)
						{
							//Audio:
							// 1. Stop background
							// 2. NoMoreMoves 1 sec
							// 3. GameOver 1 sec
							// 4. Go to main menu
							//подождём 1 сек. после того как всё успокоится
							if(IdleTime > 3000)
							{
								if(!playedNoMoreMoves)
									SoundManager.playSound(6, 1); 
								playedNoMoreMoves = true;
								NoMoreMoves = true;
							}
							
							running = IdleTime<5000;
						}
					}
					
					f = model.Fallings;
							
				} catch (InterruptedException ie) {
					running = false;
				}
			}
			
			SoundManager.playSound(3, 1);//GameOver
			
			//Intent menuIntent = new Intent(getApplicationContext(), MainMenu.class);
			//BubblesActivity.this.startActivity(menuIntent);
		}
		
		public void safeStop() {
			running = false;
			interrupt();
		}
	}

	public void deleteCell(int cellX, int cellY)
	{
		//Log.i("delectCell", cellX + "," + cellY);
		if(cellX>=0 && cellX<7 && cellY>=0 && cellY<8)
		{
	    	board.field[cellX][cellY] = 0;

			int newId = (new Random()).nextInt(7)+1;
			//board.newItemsStack[cellX]++;
			//model.addJewel(cellX*sizeX+5, (-board.newItemsStack[cellX])*sizeY+25, 1, bmp[1]);
	    	
	        for(int y=cellY-1;y>=0;y--)
		        if(cellX>=0 && cellX<=5 && y>=0 && y<=7)
		        {
		        	int id = board.field[cellX][y];
		        	board.field[cellX][y] = 0;
		        	if(id>0)
		        	{
		    	        board.newItemsStack[cellX]++;
		        		model.addJewel(cellX*sizeX+5, y*sizeY+25, id, bmp[id]);
		        	}
		        	
					newId = (new Random()).nextInt(7)+1;
			        //board.newItemsStack[cellX]++;
					//model.addJewel(cellX*sizeX+5, (-board.newItemsStack[cellX])*sizeY+25, 2, bmp[2]);
		        }
		}
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		try{
			//if(!model.Fallings)
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
		
			        int cellX = (int)((event.getX()-5) / sizeX); 
			        int cellY = (int)((event.getY()-25) / sizeY); 
		
			        if(cellX>=0 && cellX<7 && cellY>=0 && cellY<8)
			        {
			        	//deleteCell(cellX, cellY);
			        	
			        	if(FirstJewel==null)
			        	{
			        		FirstJewel = new Point(cellX, cellY);
			        	}
			        	else
			        	{
			        		if((Math.abs(cellX-FirstJewel.x)==0 && Math.abs(cellY-FirstJewel.y)==1) ||
			        		   (Math.abs(cellX-FirstJewel.x)==1 && Math.abs(cellY-FirstJewel.y)==0)) {
				        		SecondJewel = new Point(cellX,  cellY);

				        		int tmp = board.field[FirstJewel.x][FirstJewel.y];
				        		board.field[FirstJewel.x][FirstJewel.y] = board.field[SecondJewel.x][SecondJewel.y];
				        		board.field[SecondJewel.x][SecondJewel.y] = tmp;
				        		
				        		int changes = CheckBoard();
				        		Log.i("CheckBoard", "Changes:" + changes);
				        		
				        		if(changes==0)
				        		{
					        		tmp = board.field[FirstJewel.x][FirstJewel.y];
					        		board.field[FirstJewel.x][FirstJewel.y] = board.field[SecondJewel.x][SecondJewel.y];
					        		board.field[SecondJewel.x][SecondJewel.y] = tmp;
				        		}
				        		
				        		FirstJewel  = null;
			        		}
			        		else
			        			FirstJewel = new Point(cellX,  cellY);
			        	}
			        	
						return true;
			        }
				}
		}
		finally{}
		return false;
	}


}