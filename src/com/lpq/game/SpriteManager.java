/*
 * SpriteManager.java
 * Joshua Speight
 * Liquid Pro Quo, 2011
 * 
 * Sprite Manager class acts as a library for the game's images, loading them up at 
 * game start, and passing them forward upon request. 
 */

package com.lpq.game;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

public class SpriteManager 
{
	private HashMap <String,BufferedImage> dictionary;
	
	public SpriteManager()
	{
		dictionary = new HashMap<String,BufferedImage>();
	}
	
	public void clearImages()
	{
		dictionary.clear();
	}
	
	//Given a path to the file containing the list of images, will load
	// each image into the dictionary, with the name of the image (w/ no extension)
	// as the key. Png is the default image type, override for customization. 
	public void loadImages(String filePath)
	{
		try 
		{
			File file = new File(filePath);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			
			while(line != null)
			{
				dictionary.put(line,ImageIO.read(new File("images/"+line+".png")));
				line = br.readLine();
			}
			
			br.close();

		} catch (IOException e) 
		{
			javax.swing.JOptionPane.showConfirmDialog((java.awt.Component)
				       null, "Problem loading game images! Please make sure" +
				       		" all images are present in the images folder and" +
				       		" try again.", "Uh-Oh!",
				       		javax.swing.JOptionPane.DEFAULT_OPTION);
			
			System.exit(0);
		}
	}
	
	//when passed in a key (the name of the image), returns that image as a buffered image
	public BufferedImage getImage(String key)
	{
		return dictionary.get(key);
	}
}
