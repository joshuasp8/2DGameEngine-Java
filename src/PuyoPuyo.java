/*
 * PuyoPuyo.java
 * Joshua Speight
 * 
 * This class initializes the game, runs the main game loop, manages game states,
 * and also acts as the canvas, drawing all the game details using two
 * buffers.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lpq.game.GameCanvas;
import com.lpq.game.GameManager;


public class PuyoPuyo extends GameCanvas
{
	// --------------- Constants ----------------
	public static final int BALLSIZE =  32;
	public static final int HUDSIZE =  48;
	
	public static final int GROUND =  384;//location of ground on screen
	
	public static final int SCREENWIDTH =  192; // 32X32 blocks, 6x12 grid
	public static final int SCREENHEIGHT =  384;// 32X32 blocks, 6x12 grid
	
	public static final int SCREENX =  500;// 32X32 blocks, 6x12 grid
	public static final int SCREENY =  200;// 32X32 blocks, 6x12 grid
	
	public static final int INTERVAL =  60;
		//------------------------------------

	private JLabel gameLabel; // label to show score and game info
	
	public PuyoPuyo()
	{
		// you can call the super constructor filled in with game details here
		// or u can choose not to have a constructor at all and just set your details
		// in the setGameDetails method.
		//super(SCREENWIDTH,SCREENHEIGHT,SCREENX,SCREENY,"Puyo-Puyo",INGAME); 
	}
	
	@Override
	public void updateGamePaused()
	{
		gameLabel.setText("PAUSED");
	}
	
	@Override
	public void updateGameOver()
	{
		gameLabel.setText("Game Over!!");
		isGameOver = true;
	}
	
	@Override
	public void updateInGame()
	{
		PuyoManager pMan = (PuyoManager) gMan;
		
		pMan.update();
		gameLabel.setText("Score: " + pMan.getScore() +
				"   Chain: " + pMan.getChainLevel());
		
		if(pMan.isGameOver())
			gameState = GAMEOVER;	
	}
	
	@Override
	public void customizeScreen()
	{
		JPanel hud = new JPanel(); // screen's info area
		hud.setPreferredSize(new Dimension(SCREENWIDTH,HUDSIZE));
		hud.setBackground(Color.LIGHT_GRAY);
		hud.setVisible(true);
		
		screen.setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT + HUDSIZE));
		screen.add(hud);
		
		gameLabel = new JLabel("Score: 0    Chain: 0");
		gameLabel.setFont(new Font("TimesRoman", Font.BOLD,16));
		hud.add(gameLabel);
	}
	
	@Override
	public void setGameDetails()
	{
		screenTitle = "Puyo-Puyo";
		screenWidth = SCREENWIDTH;
		screenHeight = SCREENHEIGHT;
		screenX = SCREENX;
		screenY = SCREENY;
		gameState = INGAME;
	}
	
	public static void main(String [] args)
	{
		new PuyoPuyo();
	}
	
	private class PuyoKeyAdapter extends KeyAdapter 
	{
		//Keys are tapped which alerts the game manager of user input to act on
		//during its update
		public void keyReleased(KeyEvent e)
		{
			PuyoManager pMan = (PuyoManager) gMan;
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) 
			{
				pMan.leftAlert();
			}
			else
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
				{
					pMan.rightAlert();
				}
				else
					if (e.getKeyCode() == KeyEvent.VK_DOWN) 
					{
						pMan.downAlert();
					}
					else
						if (e.getKeyCode() == KeyEvent.VK_Z) 
						{
							pMan.zAlert();
						}
						else
							if (e.getKeyCode() == KeyEvent.VK_X) 
							{
								pMan.xAlert();
							}
							else
								if (e.getKeyCode() == KeyEvent.VK_ENTER) 
								{
									if(gameState == INGAME)
									{
										gameState = GAMEPAUSED;
									}
									else
										if(gameState == GAMEPAUSED)
										{
											gameState = INGAME;
										}
									
								}
		
		}
	}


	@Override
	protected GameManager getGameManagerInstance()
	{
		return new PuyoManager();
	}

	@Override
	protected KeyAdapter getKeyListenerInstance()
	{
		return new PuyoKeyAdapter();
	}
}
