package com.bask.appopengl;

public class Board {
	public int SizeX = 6;
	public int SizeY = 8;
	public int[][] field = new int[SizeX][SizeY];
	public int[] newItemsStack = new int[SizeX]; 
	
	public Board(){
		for(int x=0; x<SizeX; x++)
		{
			newItemsStack[x] = 0;
			for(int y=0; y<SizeY; y++)
			{
				field[x][y] = 0;
			}
		}
	}
}
