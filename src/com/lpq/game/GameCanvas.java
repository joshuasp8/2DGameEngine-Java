/*Game2D.h
* Joshua Speight
* Liquid Pro Quo, 2011
*
* Core class of the engine. Has both Sprite and Game Manager member instances. Contains run
* method which houses game loop.
* 
* Flow of game from extended class should be as follows:
* main->constructor->getManager/Listener Instances->setDetails->customizeScreen->
* updateSplash,Start,InGame,Paused,GameOver Methods-> drawBackground,Objs,Foreground methods
*/

package com.lpq.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public abstract class GameCanvas extends Canvas
{
	// --------------- Constants ----------------
	public static final int SPLASHSCREEN =  0;
	public static final int STARTSCREEN =  1;
	public static final int INGAME =  2;
	public static final int GAMEPAUSED =  3;
	public static final int GAMEOVER =  4;
	//-------------------------------------------
	
	protected int screenWidth, screenHeight, screenX, screenY;
	protected String screenTitle; 
	
	protected BufferStrategy strategy; // for easy back buffering
	protected Graphics g; // game graphics
	
	protected JPanel screen;
	 
	//private JLabel gameLabel; // label to show score and game info
	
	protected int gameState; // current state of the game
	
	protected boolean isGameOver;
	
	//private PuyoManager pManager; // manage the on-goings of the game
	protected GameManager gMan;
	protected SpriteManager sMan; // manages images so that each obj won't have to carry
							// a copy of their image around
	
	
	//Game Canvas Default Constructor
	// This constructor is called if the child class does not provide a constructor
	//or does not specify a detailed version of the canvas to call. 
	public GameCanvas()
	{
		initGame(true); // true indicates the the default "setGameDetails" method
						//should be used
		run();
	}
	
	//Constructor with width and height provided
	public GameCanvas(int w, int h)
	{
		screenWidth = w;
		screenHeight = h;
		
		initGame(true);
		run();
	}
	
	//Constructor with width and height and gamestate provided
	public GameCanvas(int w, int h, int gs)
	{
		screenWidth = w;
		screenHeight = h;
		
		initGame(true); //tho we assume game state won't be set here then, since
					//it is provided in the constructor
		
		gameState = gs;
		
		run();
	}
	
	//Full Constructor with width,height, x and y positions, frame title and
	//game state provided
	public GameCanvas(int w, int h, int x, int y, String title, int gs)
	{
		screenWidth = w;
		screenHeight = h;
		screenX = x;
		screenY = y;
		screenTitle = title;
		gameState = gs;
		
		initGame(false); //won't call set details methods
		
		run();
	}
	
	//Begins initializing game
	private void initGame(boolean setDets)
	{
		isGameOver = false;
		
		gMan = getGameManagerInstance();
		sMan = getSpriteManagerInstance();
		sMan.loadImages("files/images.txt");
		
		if(setDets) // if false, don't bother, the details are already provided
			setGameDetails();
		
		initScreen(); // initialize the game display
		addKeyListener(getKeyListenerInstance());
	}
	
	//Sets up the screen with the provided details
	private void initScreen()
	{
		if(!detailsSet())
		{
			throw new RuntimeException("Screen Details (e.g width & height) NOT specified!!");
		}
		
		JFrame frame = new JFrame(screenTitle);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(screenX, screenY);
		
		screen = new JPanel();
		screen.setPreferredSize(new Dimension(screenWidth,screenHeight));
		screen.setBackground(Color.white);
		
		frame.setContentPane(screen);
		
		this.setBounds(0,0,screenWidth,screenHeight);
		screen.add(this);
		
		this.setIgnoreRepaint(true); //I'll handle the painting manually
		this.requestFocus(); //in case we don't already have it
		
		customizeScreen(); // now that the main screen is set up, additions may be made
		
		this.createBufferStrategy(2); // 2 buffers
		strategy = getBufferStrategy();
		
		frame.pack();
	}
	
	// Confirms setScreenDetails method has been properly implemented,
	// meaning at least a screen height and width has been explicitly set 
	private boolean detailsSet()
	{
		if(screenHeight == 0 && screenWidth == 0)
			return false;
		
		return true;
	}
	
	//Main Game Loop
	//if the game isn't over will loop and excute state-based logic frame by frame
	//ending each frame with a call to paint
	private void run()
	{
		while(!isGameOver)
		{
			if(gameState == SPLASHSCREEN)
				updateSplashScreen();
			if(gameState == STARTSCREEN)
				updateStartScreen();
			if(gameState == INGAME)
				updateInGame();
			if(gameState == GAMEPAUSED)
				updateGamePaused();
			if(gameState == GAMEOVER)
				updateGameOver();
			
			paint();
		}
		
	}
	
	
	//-------------- ABSTRACT METHODS ----------------
	// logic that occurs every frame while the In-Game portion of the game is running
	// usually this is a call to the game manager's update method
	// followed by any extra GUI maintenance, etc.
	// And concluding with a check of the Game Manager's isGameOver status, changing
	// the gameState to gameOver when a true is received
	protected abstract void updateInGame();
	
	// how the update method reacts on a Game Over game state
	// usually we just set isGameOver to true, and give a visual game over cue.
	protected abstract void updateGameOver();
	
	//return a new Custom Key Adapter, this should be made as a private class
	protected abstract KeyAdapter getKeyListenerInstance(); 
	
	// return a custom instance of the abstract Game Manager
	protected abstract GameManager getGameManagerInstance();
		//--------------------------------------------------------
	
	//-------------- OPTIONAL METHODS ----------------
	//These methods are available and may likely be overridden since most will do
	//nothing. However, they will be not be set to abstract so, it will fine to
	// ignore them in some cases.
	// return custom or default instance of a Sprite Manager
	
	
	// may be overridden in favor of a custom spriteManager to return instead
	protected SpriteManager getSpriteManagerInstance()
	{
		return new SpriteManager();
	}
	
	//set screen width, height, x, y, title, also gameState, and anything appropriate
	// Note: This method does not get called if the gameCanvas is made using the
	// full constructor with all screen details since this would no longer be neccessary
	protected void setGameDetails()
	{
		// screen defaults
		screenX = 400;
		screenY = 200;
		screenTitle = "";
		gameState = INGAME;
		// width and height have been intentionally omitted, since it's important
		// to set up basic game details here manually, if the full game constructor
		// was not used.	
	}
	
	//Works with the buffer strategy to draw the game to the screen. Made up of 3 components,
	// drawBackground, drawObjs , and draw Foreground, which all may be overriden as
	// necessary
	private void paint()
	{
		g = strategy.getDrawGraphics();
		
		drawBackground();
		drawObjs();
		drawForeground();
		
		g.dispose();
		strategy.show();
	}
	
	//this may be overrode if all that is needed is a different background than the
	//plain white bg, that is provided by default. In cases where paint() is
	//overrode this method will end up ignored anyway. 
	protected void drawBackground()
	{
		g.setColor(Color.white);
		g.fillRect(0,0,screenWidth,screenHeight);

		if(gMan.getBg() != null)
			g.drawImage(gMan.getBg(),0,0,null);
		
		for(GameObj o : gMan.getBgObjs())
		{
			g.drawImage(sMan.getImage(o.getImageName()),o.getX(),o.getY(),null);
		}
	}
	
	//draws foreground and fgObjs, may be overridden if necessary 
	protected void drawForeground()
	{
		if(gMan.getFg() != null)
			g.drawImage(gMan.getFg(),0,0,null);
		
		for(GameObj o : gMan.getFgObjs())
		{
			g.drawImage(sMan.getImage(o.getImageName()),o.getX(),o.getY(),null);
		}
	}
	
	// draws player and game objects to the screen
	protected void drawObjs()
	{
		if(gMan.getPlayer() != null)
		{
			GameObj p = gMan.getPlayer();
			g.drawImage(sMan.getImage(p.getImageName()), p.getX(), p.getY(), null);
		}
		
		for(int i = 0; i < gMan.getObjs().size(); i++)
		{
			GameObj temp = gMan.getObjs().get(i);
			g.drawImage(sMan.getImage(temp.getImageName()),temp.getX(),temp.getY(),null);
		}
	}
	
	// Add additional details to the screen such as text, panels, etc.
	// The key here is this is called after the screen has already instantiated
	protected void customizeScreen()		{}
	
	// logic performed every frame of the splash screen(s)
	// this is usually management of what image should be shown and for how long
	// using a counter or something similar to inform when states/images should be
	// changed
	protected void updateSplashScreen()		{}
	
	// logic performed every frame while the game is on the start screen
	// logic here is usually lack-luster, not doing much, and basically waiting
	// for the key listener to change state to INGAME
	protected void updateStartScreen()		{}
	
	// logic that occurs every frame while the game state is set to paused
	// usually just a visual "Paused" cue, and a lack on in-game logic
	protected void updateGamePaused() {}
	
		//------------------------------------------------------
	
}
