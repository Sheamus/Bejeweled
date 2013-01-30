package com.bask.appopengl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class JewelModel implements OnCompletionListener {
	public int Column;
	public int Row;
	public int Id;
	public boolean IsFalling;
	public int y;
	public int oldRow;
	public int newRow;
	
	public boolean Fallings = false;
	public boolean Selected = false;
	
	private Board board;

	//private static final float INITIAL_RADIUS = 20f;
	//private static final float MAX_RADIUS = 100f;
	
	// higher numbers make the balls expand faster
	//private static final float RADIUS_CHANGE_PER_MS = .08f;
	
	private final List<MediaPlayer> players = new LinkedList<MediaPlayer>();
	private boolean running = false;
	
	public static final class Jewel {
		float x, y;
		Bitmap bmp;
		int id;
		
		public Jewel(float x, float y, int id, Bitmap bmp) {
			this.x = x;
			this.y = y;
			this.id = id;
			this.bmp = bmp;
			//radius = INITIAL_RADIUS;
		}
	}
	
	private final List<Jewel> jewels = new LinkedList<Jewel>();

    private volatile long lastTimeMs = -1;
	
	public final Object LOCK = new Object();
	
	public JewelModel(Board board) {
		this.board = board;
	}
	
	public void onResume(Context context) {
		synchronized (LOCK) {
			for (int i=0; i<4; i++) {
				MediaPlayer mp = MediaPlayer.create(context, R.raw.pop);
				mp.setVolume(1f, 1f);
				players.add(mp);
				try {
					mp.setLooping(false);
					mp.setOnCompletionListener(this);
					
					// TODO: there is a serious bug here. After a few seconds of
					// inactivity, we see this in LogCat:
					//   AudioHardwareMSM72xx Going to standby 
					// then the sounds don't play until you click several more
					// times, then it starts working again
					
				} catch (Exception e) {
					e.printStackTrace();
					players.remove(mp);
				}
			}
			running = true;
		}
	}
	
	public void onPause(Context context) {
		synchronized (LOCK) {
			running = false;
			for (MediaPlayer p : players) {
				p.release();
			}
			players.clear();
		}
	}
	
	public List<Jewel> getJewels() {
		synchronized (LOCK) {
			return new ArrayList<Jewel>(jewels);
		}
	}
	
	public void addJewel(float x, float y, int id, Bitmap bmp) {
		synchronized (LOCK) {
			jewels.add(new Jewel(x, y, id, bmp));
		}
	}
	
	public void setSize(int width, int height) {
		// TODO ignore this for now...we could hide bubbles that
		// are out of bounds, for example
	}

    public void updateJewels() {
        long curTime = System.currentTimeMillis();
        if (lastTimeMs < 0) {
            lastTimeMs = curTime;
            // this is the first reading, so don't change anything
            return;
        }
        long elapsedMs = curTime - lastTimeMs;
        lastTimeMs = curTime;
        
        //final float radiusChange = elapsedMs * RADIUS_CHANGE_PER_MS;

        MediaPlayer mp = null;

    	synchronized (LOCK) {
    		//Set<Jewel> victims = new HashSet<Jewel>();
    		ArrayList<Jewel> victims = new ArrayList<Jewel>();
    		
    		for (Jewel j : jewels) {
    			j.y += 5;
    			//b.radius += radiusChange;
    	        int cellX = (int)((j.x-5) / 50); 
    	        int cellY = (int)((j.y-25) / 50)+1;
    	        
    	        //if(cellX>=0 && cellX<=5 && cellY>=0 && cellY<=7)
    	        {
    	        	int id = 1; 
    	        	if(cellY<=7 && cellY>=0) 
    	        		id = board.field[cellX][cellY];
    	        	if(id>0)
    	        	{
    	        		//Log.i("JewelModel", "Удаляем "+j.id+" ["+ cellX +", " + cellY + "] " + id + "=>" + this.Id);
    	        		victims.add(j);
    	        		if(cellY>0 && j.id>0)
    	        			board.field[cellX][cellY-1] = j.id;
    	        		//SoundManager.playSound(7, 1);
    	        	}
    	        }
    		}
    		
    		if (victims.size() > 0) {
    			for(int i=0;i<victims.size();i++)
    			{
    				Jewel jw = victims.get(i);
        	        int cellX = (int)((jw.x-5) / 50); 
    				//Log.i("victims", "column=" + cellX);
	    			//Log.i("updateJewels", "board.newItemsStack[cellX]=" + board.newItemsStack[cellX]--);
	    			board.newItemsStack[cellX] = 0;
    			}
    			jewels.removeAll(victims);
    			// since a bubble popped, try to get a media player
    			if (!players.isEmpty()) {    				
    				mp = players.remove(0);
    			}
    		}
    	}
    	
    	Fallings = jewels.size() > 0;
    	
    	if (mp != null) {
    		mp.start(); 
    	}
    }

	public void onCompletion(MediaPlayer mp) {
		synchronized (LOCK) {
			if (running) {
	    		mp.seekTo(0);
				//System.out.println("on completion!");
	    		// return the player to the pool of available instances
				players.add(mp);
			}
		}
	}
}
