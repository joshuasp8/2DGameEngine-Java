/*
 * Ball.java
 * Joshua Speight
 * 
 * The Ball class represents each of the colored smiley faces that fall down on
 * screen throughout the game. Each ball keeps an updated reference to its neighbors
 * and position on the 6 x 12 grid via an index, for quick look ups, avoiding
 * many calculations. 
 */

import java.util.ArrayList;
import java.util.List;

import com.lpq.game.GameManager;
import com.lpq.game.GameObj;


public class Ball extends GameObj
{
	//------------ CONSTANTS --------------
	public static final int RED = 0; 
	public static final int BLUE = 1; 
	public static final int GREEN = 2; 
	public static final int YELLOW = 3;
	
	public static final int INITFALL = 0;
	public static final int SEATED = 1;
	public static final int FALLING = 2;
	
	public static final int DELAY = 485; //arbitrary value to set falling speed
		//--------------------------------------
	
	private int index; // this obj's position in PuyoManager's list; useful for many 
						//needed positioning calculations and references
	private int color;
	private int fallTimer;
	
	private Ball partner; //points to other ball that falls in the initial pair;
	private List<Ball> neighbors; // surrounding balls;
	
	//------------- GETTERS/SETTERS -------------
	
	//converts location to a value on a 6x12 grid
	public int gridX()					{return (x/PuyoPuyo.BALLSIZE);}
	public int gridY()					{return (y/PuyoPuyo.BALLSIZE);}
	
	public int getColor()				{return color;}
	public int getIndex()				{return index;}
	
	public Ball getPartner()			{return partner;}
	public List<Ball> getNeighbors()	{return neighbors;}
	
	public Ball getNeighbor(int i)		{return neighbors.get(i);} // get specific neighbor
	
	public void setColor(int i)			{color = i;}
	public void setIndex(int i)			{index = i;}
	
	public void setPartner(Ball i)		{partner = i;}
	public void clearNeighbors()		{neighbors.clear();}
	public void addNeighbor(Ball i)		{neighbors.add(i);}
		//-----------------------------------------
	
	public Ball(int clr, int i)
	{
		color = clr;
		index = i;
		
		state = INITFALL;
		fallTimer = DELAY;
		
		switch(color)
		{
		case RED: imageName = "rit_puyo_red";break;
		case GREEN: imageName = "rit_puyo_green";break;
		case BLUE: imageName = "rit_puyo_blue";break;
		case YELLOW: imageName = "rit_puyo_yellow";
		}
		
		neighbors = new ArrayList<Ball>();
	}
	
	public void update(GameManager gm)
	{		
		if(state == INITFALL || state == FALLING)
		{
			fall(((PuyoManager) gm).getGrid());
		}
	}
	
	
	
	public void fall(int[][] grid)
	{
		if(y + PuyoPuyo.BALLSIZE >= PuyoPuyo.GROUND) //hit or passed the ground?
		{
			y = PuyoPuyo.GROUND - PuyoPuyo.BALLSIZE;
			state = SEATED;
		}
		//not yet on the ground? let's check our grid to look out for possible
		//collisions then
		if(x >= 0 && y >= 0) //are we at least on the screen?
		{
			if(grid[gridX()][gridY()] != -1)//something there?
			{
				//then
				y -= 32; //backtrack 1 spot
				state = SEATED; //have a seat there
			}
		}
		
		if(state == SEATED)
		{
			// now that this ball has found a seat in the grid, lets calculate its
			//grid position and record our index position there for later
			if(x >= 0 && y >= 0) //make sure we're registering for a legit grid spot
			{
				grid[gridX()][gridY()] = index;
			}
			
			fallTimer = DELAY;
			return; // then no longer a need to fall
		}
		
		if(fallTimer == 0)
		{
			incY(32); // fall gradually
			fallTimer  = DELAY;
		}
		
		fallTimer--;			
	}
	
}
