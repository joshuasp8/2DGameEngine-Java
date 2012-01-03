/*GameManager.java
* Joshua Speight
* Liquid Pro Quo, 2011
*
* Class responsible for regulating the logic of the game. Contains references to the player
* object, the background and foreground images, background and foreground objects as well as the
* main objects active in the game. The game manager should basically update all objects when
* appropriate and once a frame check for a game over condition and update its gameover bool to
* true upon finding one.
*/

package com.lpq.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class GameManager 
{
	protected ArrayList<GameObj> objs; // list of main objects in the game
	protected ArrayList<GameObj> bgObjs; // list of background objs; note these objs do not get to call their update methods
	protected ArrayList<GameObj> fgObjs; // list of foreground objs; note these objs do not get to call their update methodse
	protected boolean isGameOver;
	protected GameObj player; // seen as "key" object to a game
	protected BufferedImage currBg;
	protected BufferedImage currFg;
	
	public ArrayList<GameObj> getObjs()		{return objs;}
	public ArrayList<GameObj> getBgObjs()	{return bgObjs;}
	public ArrayList<GameObj> getFgObjs()	{return fgObjs;}
	public GameObj getPlayer()				{return player;}
	public BufferedImage getBg()			{return currBg;}
	public BufferedImage getFg()			{return currFg;}
	public boolean isGameOver()				{return isGameOver;}
	
	// update function by default updates all game objects, then the player, and finally checks the game to
	// see if there are any changes to the game over condition
	// may be suitable for some games, but likely will need to be overridden to allow for more complexity
	public GameManager()
	{
		objs = new ArrayList<GameObj>();
		bgObjs = new ArrayList<GameObj>();
		fgObjs = new ArrayList<GameObj>();
		player = null;
		currBg = null;
		currFg = null;
		isGameOver = false;
	}
	
	// update function by default updates all game objects, then the player, and finally checks
	//the game to see if there are any changes to the game over condition
	// may be suitable for some games, but likely will need to be overridden to allow for more complexity
	public void update()
	{
		//NOTE: by default, the background and foreground objects do NOT get calls to their update methods
		// since they are considered scenery. This will imply no movement/animation, etc
		for(GameObj obj : objs)
		{
			obj.update(this);
		}
		
		if(player != null)
		{
			player.update(this);
		}
		
		isGameOver = checkGameOver();
	}
	
	// convenience method to pull an obj from the main list at index i
	public GameObj get(int i)
	{
		return objs.get(i);
	}
	
	public void addObj(GameObj o)
	{
		objs.add(o);
	}
	
	public void addFgObj(GameObj o)
	{
		fgObjs.add(o);
	}
	
	public void addBgObj(GameObj o)
	{
		bgObjs.add(o);
	}
	
	// checks the status of the game and sets isGameOver appropriately
	public abstract boolean checkGameOver();
}
