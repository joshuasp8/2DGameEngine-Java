/*
 * Puyo Manager.java
 * Joshua Speight
 * 
 * This class acts as the game logic manager, keeping track of each ball on screen,
 * general game mechanics, as well as user info like score. The class also manages
 * a 6 x 12 grid of the the game screen, used for plotting ball positions, allowing
 * for instant look-ups, allowing an easy method of neighbor detection and the
 * avoidance of other expensive calculations like collision detection.   
 */

import java.util.ArrayList;
import java.util.List;

import com.lpq.game.GameManager;
import com.lpq.game.GameObj;



public class PuyoManager extends GameManager
{
	public static final int POINTS = 50; // amount each ball is worth
	public static final int DEFAULT_CHAIN_LEVEL = 0;
	
	private int score;
	private int chainLevel;
	
	private boolean resolving;// true when the game is busy resolving a proper color link
	
	private List<Ball> stackList; // used to hold refernces during our DFS of the grid
	private List<Integer> visited; // keeps track of already visited nodes on the grid
								// during traversal for efficiency
	
	private Ball faller; // pointer to current main falling ball
	
	private int[][] grid;//let's map out the grid, to avoid calculations and provide convenience
	
	private boolean leftAlert, rightAlert, downAlert, zAlert, xAlert;// button pressed?

	public int getScore()				{return score;}
	public int getChainLevel()			{return chainLevel;}
	public Ball getFaller()				{return faller;}
	public int[][] getGrid()			{return grid;}
	
	public void leftAlert()				{leftAlert = true;}
	public void rightAlert()			{rightAlert = true;}
	public void downAlert()				{downAlert = true;}
	public void xAlert()				{xAlert = true;}
	public void zAlert()				{zAlert = true;}
	
	public PuyoManager()
	{
		super();
		score = 0;
		chainLevel = DEFAULT_CHAIN_LEVEL;
		stackList = new ArrayList<Ball>();
		visited = new ArrayList<Integer>();
		
		grid = new int[6][12];

		emptyGrid(); // sets all values to -1 to represent empty
	}
	
	@Override
	public void update()
	{
		if(faller != null)
		{	
			//have both of the most recent balls settled yet?
			if(faller.getState() == Ball.SEATED && faller.getPartner().getState() == Ball.SEATED)
			{
				faller.setPartner(null); // get rid of this reference
				faller = null;
				isGameOver = checkGameOver();
				resolving = true;
			}
		}
		else
		{
			if(checkAllGrounded()) // make sure everything has settled before we move forward
			{
				updateNeighbors();
				resolving = resolveMatches();
				if(!resolving) // done with all resolutions?
					generatePair();
			}
		}
		
		//take user input into account
		if(leftAlert)
		{
			this.moveFallerLeft();
			leftAlert = false;
		}
		else
			if(rightAlert)
			{
				this.moveFallerRight();
				rightAlert = false;
			}
			else
				if(downAlert)
				{
					this.moveFallerDown();
					downAlert = false;
				}
				else
					if(zAlert)
					{
						this.rotateCounterClockwise();
						zAlert = false;
					}
					else
						if(xAlert)
						{
							this.rotateClockwise();
							xAlert = false;
						}
		
		// now update each ball
		for(GameObj b : objs)
		{
			b.update(this);
		}
	}
	
	//returns true if every ball is seated
	public boolean checkAllGrounded()
	{
		for(GameObj b : objs)
		{
			if(b.getState() != Ball.SEATED)
				return false;
		}
		
		return true;
	}
	
	//fills each grid entry with our empty value (-1)
	public void emptyGrid()
	{
		//initialize grid values to -1  to represent Empty
		for(int i = 0; i < 6; i++)
			for(int j = 0; j < 12; j++)
				grid[i][j] = -1;
	}
	
	//uses each ball's info to bring the grid's content up to date
	public void updateGrid()
	{
		emptyGrid();
		
		for(int i = 0; i < objs.size(); i++)
		{
			((Ball)objs.get(i)).setIndex(i);
			grid[((Ball)objs.get(i)).gridX()][((Ball)objs.get(i)).gridY()] = i;
		}
	}
	
	//Creates two new random ball objects at the top of the screen 
	public void generatePair()
	{
		int color1 = (int)(Math.random()*4); // assign random colors
		int color2 = (int)(Math.random()*4);
		
		Ball b1 = new Ball(color1, objs.size());
		Ball b2 = new Ball(color2, objs.size()+1);
		
		b1.setX(96); //start at 4th column
		b1.setY(0);
		b2.setX(96);
		b2.setY(-32);
		
		faller = b1;
		b1.setPartner(b2); // secondary ball that will fall along with the faller
						// 1-way relationship, as the partner does not need
						//to be aware of the first ball
		objs.add(b1);
		objs.add(b2);
		
		chainLevel = DEFAULT_CHAIN_LEVEL;
	}
	
	//Searches grid and uses it's various helper methods to
	//update each ball on its connected neighbors
	//update graphics
	//goes through each ball and uses grid to update the neighbor list
	public void updateNeighbors()
	{
		for(int i = 0; i < objs.size(); i++)
		{
			Ball b = (Ball)objs.get(i);
			b.clearNeighbors();

			//for efficiency, let's figure out where the ball is to avoid useless checks
			//first at the top
			if(b.getY() == 0)
				updateNeighborsHelpTop(b);
			else
			//next the bottom row
				if(b.getY() == PuyoPuyo.SCREENHEIGHT - 32)
					updateNeighborsHelpBottom(b);
				else
				//left side?; note the corner cases have already been handled	
					if(b.getX() == 0)
						updateNeighborsHelpLeft(b);
					else
					//right side?; note the corner cases have already been handled	
						if(b.getX() == PuyoPuyo.SCREENWIDTH - 32)
							updateNeighborsHelpRight(b);
						//Anywhere else in the middle	
							else
								updateNeighborsHelp(b);
		}
	}
	
	//Helper Method for updating neighbors
	public void updateNeighborsHelp(Ball b)
	{
		int neighbor1, neighbor2, neighbor3, neighbor4;
		
		neighbor1 = grid[b.gridX()][b.gridY()+1];
		neighbor2 = grid[b.gridX()][b.gridY()-1];
		neighbor3 = grid[b.gridX()+1][b.gridY()];
		neighbor4 = grid[b.gridX()-1][b.gridY()];
		if(neighbor1 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor1));
		if(neighbor2 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor2));
		if(neighbor3 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor3));
		if(neighbor4 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor4));
	}
	
	//Helper Method for updating neighbors
	public void updateNeighborsHelpTop(Ball b)
	{
		int neighbor1, neighbor2, neighbor3;
		
		if(b.getX() == 0) //top left corner?
		{
			neighbor1 = grid[b.gridX()][b.gridY()+1];
			neighbor2 = grid[b.gridX()+1][b.gridY()];
			if(neighbor1 != -1) //someone there?
				b.addNeighbor((Ball)objs.get(neighbor1));
			if(neighbor2 != -1) //someone there?
				b.addNeighbor((Ball)objs.get(neighbor2));
		}
		else
			if(b.getX() == PuyoPuyo.SCREENWIDTH - 32) //top right corner?
			{
				neighbor1 = grid[b.gridX()][b.gridY()+1];
				neighbor2 = grid[b.gridX()-1][b.gridY()];
				if(neighbor1 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor1));
				if(neighbor2 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor2));
			}
			else //anywhere else up top
			{
				neighbor1 = grid[b.gridX()][b.gridY()+1];
				neighbor2 = grid[b.gridX()+1][b.gridY()];
				neighbor3 = grid[b.gridX()-1][b.gridY()];
				if(neighbor1 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor1));
				if(neighbor2 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor2));
				if(neighbor3 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor3));
			}
	}
	
	//Helper Method for updating neighbors
	public void updateNeighborsHelpBottom(Ball b)
	{
		int neighbor1, neighbor2, neighbor3;
		
		if(b.getX() == 0) //bottom left corner?
		{
			neighbor1 = grid[b.gridX()][b.gridY()-1];
			neighbor2 = grid[b.gridX()+1][b.gridY()];
			if(neighbor1 != -1) //someone there?
				b.addNeighbor((Ball)objs.get(neighbor1));
			if(neighbor2 != -1) //someone there?
				b.addNeighbor((Ball)objs.get(neighbor2));
		}
		else
			if(b.getX() == PuyoPuyo.SCREENWIDTH - 32) //bottom right corner?
			{
				neighbor1 = grid[b.gridX()][b.gridY()-1];
				neighbor2 = grid[b.gridX()-1][b.gridY()];
				if(neighbor1 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor1));
				if(neighbor2 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor2));
			}
			else //anywhere else up down there
			{
				neighbor1 = grid[b.gridX()][b.gridY()-1];
				neighbor2 = grid[b.gridX()+1][b.gridY()];
				neighbor3 = grid[b.gridX()-1][b.gridY()];
				if(neighbor1 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor1));
				if(neighbor2 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor2));
				if(neighbor3 != -1) //someone there?
					b.addNeighbor((Ball)objs.get(neighbor3));
			}
	}
	
	//Helper Method for updating neighbors
	public void updateNeighborsHelpLeft(Ball b)
	{
		int neighbor1, neighbor2, neighbor3;
		
		neighbor1 = grid[b.gridX()][b.gridY()+1];
		neighbor2 = grid[b.gridX()][b.gridY()-1];
		neighbor3 = grid[b.gridX()+1][b.gridY()];
		if(neighbor1 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor1));
		if(neighbor2 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor2));
		if(neighbor3 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor3));
	}
	
	//Helper Method for updating neighbors
	public void updateNeighborsHelpRight(Ball b)
	{
		int neighbor1, neighbor2, neighbor3;
		
		neighbor1 = grid[b.gridX()][b.gridY()+1];
		neighbor2 = grid[b.gridX()][b.gridY()-1];
		neighbor3 = grid[b.gridX()-1][b.gridY()];
		if(neighbor1 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor1));
		if(neighbor2 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor2));
		if(neighbor3 != -1) //someone there?
			b.addNeighbor((Ball)objs.get(neighbor3));
	}
	
	//Checks for a losing game condition. This would occur when a ball has been
	//stacked too high and as a result seated off-screen
	public boolean checkGameOver()
	{
		for(int i = 0; i < objs.size(); i++)
		{
			if(objs.get(i).getState() == Ball.SEATED)
			{
				if(objs.get(i).getY() < 0) //stacked too high?
					return true; // then game over!
			}
		}
		
		return false;
	}
	
	//We perform a Depth First Search on our grid looking for a color link of
	//appropriate length, popping each ball in the link when found, then updating
	//our grid
	public boolean resolveMatches()
	{		
		stackList.clear(); // start fresh for this round
		boolean found  = false; // true when a pattern of 4+ is found
		
		for(int i = 0; i < objs.size(); i++)
		{
			Ball b = (Ball)objs.get(i);
			int currColor = b.getColor();
			boolean skip = false;
			
			//to avoid wasting a huge amount of time re-checking nodes of a too-short
			//link, lets make sure this ball is one we haven't already seen
			for(int j = 0; j < visited.size(); j++)
			{
				if(b.getIndex() == visited.get(j))// seen this 1 before?
				{
					skip = true;// then don't bother
					break;
				}
			}
			if(skip)
			{
				skip = false;
				continue;
			}
			
			stackList.add(b); // add to our link's stack
			visited.add(b.getIndex()); // add to the visited list
			
			//Examine all neighbors for a match
			for(int j = 0; j < b.getNeighbors().size(); j++)
			{
				Ball b2 = b.getNeighbor(j);
				if(b2.getColor() == currColor)
				{
					stackList.add(b2); //then add to stack
					visited.add(b2.getIndex());
					
					innerResolve(b2); // now let's search this member of the chain
				}
			}
			
			if(stackList.size() >= 4)
			{
				found = true;
				break;
			}
			else
				stackList.clear();// otherwise start fresh and move on
		}
		
		visited.clear(); // done with this list for now
		
		if(found)
		{
			// now we pop all elements on the list, and award points appropriately
			for(int i = 0; i < stackList.size();i++)
			{
				Ball temp = stackList.get(i);
				objs.remove(temp);
				score += POINTS * (chainLevel+1);
			}
			
			updateBallIndicies();
			emptyGrid();
			
			for(int i = 0; i < objs.size(); i++)
			{
				objs.get(i).setState(Ball.FALLING);
			}
			
			chainLevel++;
			return true; // we need to search again since a new pattern may have resulted
		}
		else
			return false; // no matches currently on the board
		
	}
	
	//Recursively searches through each ball's neighbors for common color links
	public void innerResolve(Ball b)
	{
		boolean skip = false; // tells us if we've been at this node already
		
		for(int i = 0; i < b.getNeighbors().size(); i++)
		{
			Ball b2 = b.getNeighbor(i);
			
			for(int j = 0; j < visited.size();j++)
			{
				if(b2.getIndex() == visited.get(j)) //have we already examined this ball?
				{
					skip = true;
					break;
				}
			}
			if(skip)
			{
				skip = false;
				continue;
			}
			
			if(b2.getColor() == b.getColor())
			{
				stackList.add(b2);
				visited.add(b2.getIndex());
				innerResolve(b2);
			}
		}
	}
	
	//used to keep accurate values after balls have been popped from list
	public void updateBallIndicies()
	{
		for(int i = 0; i < objs.size(); i++)
		{
			((Ball)objs.get(i)).setIndex(i);
		}
	}
	
	public void moveFallerLeft()
	{
		if(faller != null && faller.getPartner() != null && faller.getState() == Ball.INITFALL 
				&& faller.getPartner().getState() == Ball.INITFALL)
		{
			if(faller.getY() < 0) //just ignore if offscreen
				return;
			
			if(faller.getX() <= 0 || faller.getPartner().getX() <= 0)//at edge?
				return; // then nevermind
			
			//lets also check the grid for ball interferences
			if(faller.getPartner().getX() < faller.getX()) //special case where partner is to the left
			{
				//someone already there?
				if(grid[faller.getPartner().gridX()-1][faller.getPartner().gridY()] != -1) 
					return;
			}
			else
			{
				if(grid[faller.gridX()-1][faller.gridY()] != -1) //someone already there?
					return;
			}
			
			//Nothing in the way?
			faller.incX(-32);
			faller.getPartner().incX(-32);
		}
	}
	
	public void moveFallerDown()
	{
		if(faller != null && faller.getPartner() != null && faller.getState() == Ball.INITFALL 
				&& faller.getPartner().getState() == Ball.INITFALL)
		{
			if(faller.getY() < 0) //just ignore if offscreen
				return;
			
			if(faller.getY()+PuyoPuyo.BALLSIZE >= PuyoPuyo.SCREENHEIGHT 
				|| faller.getPartner().getX()+PuyoPuyo.BALLSIZE >= PuyoPuyo.SCREENHEIGHT)//at edge?
				return; // then nevermind

			//Otherwise	
			faller.incY(+32);
			faller.getPartner().incY(+32);
		}
	}
	
	public void moveFallerRight()
	{
		if(faller != null && faller.getPartner() != null && faller.getState() == Ball.INITFALL 
				&& faller.getPartner().getState() == Ball.INITFALL)
		{
			if(faller.getY() < 0) //just ignore if offscreen
				return;
			
			if(faller.getX()+PuyoPuyo.BALLSIZE >= PuyoPuyo.SCREENWIDTH 
				|| faller.getPartner().getX()+PuyoPuyo.BALLSIZE >= PuyoPuyo.SCREENWIDTH)//at edge?
				return; // then nevermind
			
			//lets also check the grid for ball interferences
			if(faller.getPartner().getX() > faller.getX()) //special case where partner is to the right
			{
				//someone already there?
				if(grid[faller.getPartner().gridX()+1][faller.getPartner().gridY()] != -1)
					return;
			}
			else
			{
				if(grid[faller.gridX()+1][faller.gridY()]!= -1) //someone already there?
					return;
			}
			
			//Nothing in the way?	
			faller.incX(+32);
			faller.getPartner().incX(+32);
		}
	}
	
	//Clockwise Rotation method for the falling balls
	//balls will always be in sync on either the x or y axis, so method searches
	//for difference in position and adjusts appropriately
	public void rotateClockwise()
	{
		if(faller == null || faller.getPartner() == null || faller.getState() != Ball.INITFALL 
				|| faller.getPartner().getState() != Ball.INITFALL )
			return; // any of the above cases will be cause to negate the rotation
		
		if(faller.getY() < 0) //just ignore if offscreen
			return;
		
		Ball partner = faller.getPartner();
		
		if(partner.getX() < faller.getX())// is it to the left?
		{
			//if(grid[faller.getX()/32][(faller.getY()-32)/32] != -1) //something there?
			//	return;
			// ^ no need to check this case
			
			partner.setX(faller.getX());
			partner.setY(faller.getY()-32);
		}
		else
		if(partner.getX() > faller.getX())// is it to the right?
		{
			if(grid[faller.gridX()][faller.gridY()+1] != -1) //something there?
				return;
			
			partner.setX(faller.getX());
			partner.setY(faller.getY()+32);
		}
		else
		if(partner.getY() < faller.getY())// is it above?
		{
			if(faller.getX()+32 == PuyoPuyo.SCREENWIDTH)//at right edge?
				return;
			
			if(grid[faller.gridX()+1][faller.gridY()] != -1) //something there?
				return;
			
			partner.setX(faller.getX()+32);
			partner.setY(faller.getY());
		}
		else
		if(partner.getY() > faller.getY())// is it below?
		{
			if(faller.getX() == 0)//at left edge?
				return;
			
			if(grid[faller.gridX()-1][faller.gridY()] != -1) //something there?
				return;
			
			partner.setX(faller.getX()-32);
			partner.setY(faller.getY());
		}	
	}
	
	//CounterClockwise Rotation method for the falling balls
	public void rotateCounterClockwise()
	{
		if(faller == null || faller.getPartner() == null || faller.getState() != Ball.INITFALL 
				|| faller.getPartner().getState() != Ball.INITFALL )
			return; // any of the above cases will be cause to negate the rotation
		
		if(faller.getY() < 0) //just ignore if offscreen
			return;
		
		Ball partner = faller.getPartner();
		
		if(partner.getX() < faller.getX())// is it to the left?
		{
			if(grid[faller.gridX()][faller.gridY()+1] != -1) //something there?
				return;
			
			partner.setX(faller.getX());
			partner.setY(faller.getY()+32);
		}
		else
		if(partner.getX() > faller.getX())// is it to the right?
		{
			//if(grid[faller.getX()/32][(faller.getY()-32)/32] != -1) //something there?
			//	return;
			//^ no need to check here
			
			partner.setX(faller.getX());
			partner.setY(faller.getY()-32);
		}
		else
		if(partner.getY() < faller.getY())// is it above?
		{
			if(faller.getX() == 0)//at left edge?
				return;
			
			if(grid[faller.gridX()-1][faller.gridY()] != -1) //something there?
				return;
			
			partner.setX(faller.getX()-32);
			partner.setY(faller.getY());
		}
		else
		if(partner.getY() > faller.getY())// is it below?
		{
			if(faller.getX() + PuyoPuyo.BALLSIZE == PuyoPuyo.SCREENWIDTH)//at right edge?
				return;
			
			if(grid[faller.gridX()+1][faller.gridY()] != -1) //something there?
				return;
			
			partner.setX(faller.getX()+32);
			partner.setY(faller.getY());
		}	
	}
}
