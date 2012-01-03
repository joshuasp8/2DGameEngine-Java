/*GameObj.java
* Joshua Speight
* Liquid Pro Quo, 2011
* 
* Game Objects are the basic pieces that operate under the Game Manager's supervision. They are
* regulated mainly by their update method which should determine almost all of their functionality.
* This method will do drastically different things depending on the game and what piece this
* object plays in the game.
* 
*/

package com.lpq.game;

public abstract class GameObj 
{
	protected int x, y;
	protected int state;
	protected String imageName;
	
	//------------- ACCESSORS -------------
	public int getX()					{return x;}
	public int getY()					{return y;}
	public int getState()				{return state;}
	public String getImageName()		{return imageName;}
	
	public void setX(int i)				{x = i;}
	public void setY(int i)				{y = i;}
	public void setImageName(String i)	{imageName = i;}
	public void setState(int i)			{state = i;}
	
	public void incX(int i)				{x += i;}
	public void incY(int i)				{y += i;}
	//------------------------------------------
	
	// update is called each frame by game manager 
	public abstract void update(GameManager gMan);
}
