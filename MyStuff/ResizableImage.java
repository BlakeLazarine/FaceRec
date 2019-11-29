package MyStuff;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color; 
import javax.imageio.ImageIO;
 
/*
	by Blake Lazarine
	This class is used to make operations with images easier
 */
public class ResizableImage {
 
    private BufferedImage img;

    //standard constructor
 	public ResizableImage(BufferedImage image)
	{
		img = image;

	}


	//Constructor that is used when preparing images for use as a training set. This makes the image greyscale, lower resolution, and cropped
	public ResizableImage(BufferedImage image, boolean modify)
	{
		
		img = image;
		if(modify)
		{
			getSquare();
		}
		
		getBWSmall(100, 100);
		crop();
	}

	//constructor that converts a vector into a 2 dimensional image for given width and height
 	public ResizableImage(int w, int h, double[] vals)
 	{
 		img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
 		int count = 0;
    	for(int i = 0; i < h; i++)
    	{
    		for(int j = 0; j < w; j++)
    		{
    			double x = vals[count];
    			byte val = (byte) (Math.min(Math.max(x, 0), 255));

    			int color = (val << 16) + (val << 8) + (val);
    			img.setRGB(j, i, color);
    			count++;
    		}	
    	}
 	}

 	//sets the image to be the central square of the current image
	private void getSquare() {
   		int h = img.getHeight();
   		int w = img.getWidth();
   		System.out.println(img.getType());
   		if(h > w)
      		img = img.getSubimage(0,(h-w)/2,w,w);
   		if(w > h)
        	img = img.getSubimage((w-h)/2,0,h,h);

	}

	//crops the image to the central subimage of the current image
	private void crop()
	{
		img = img.getSubimage(30, 20, 40, 65);
		//img = img.getSubimage(25, 20, 50, 72);
	}

	//returns the image
	public BufferedImage getImg()
	{
		return img;
	}

	//makes the image greyscale and a certain resolution
	private void getBWSmall(int w, int h)
	    {
	    	BufferedImage outputImage = new BufferedImage(w,
	                h, BufferedImage.TYPE_BYTE_GRAY);
	        Graphics2D g2d = outputImage.createGraphics();
	        g2d.drawImage(img, 0, 0, w, h, null);
	        g2d.dispose();
	        img = outputImage;
	    }

	//gets the 1 dimensional representation of a greyscale image as a vector.
    public double[] getVector()
    {
   		int count = 0;
   		double[] vals = new double[img.getHeight() * img.getWidth()];
   		for(int i = 0; i < img.getHeight(); i++)
   		{
   			for(int j = 0; j < img.getWidth(); j++)
   			{
   				Color c = new Color(img.getRGB(j,i));
				vals[count] = c.getRed();
				if(c.getRed() != c.getBlue())
					System.out.println("!");
				count++;
   			}
   		}
   		return vals;
    }

    //returns the image field
    public BufferedImage getImage()
   {
   	return img;
   }

    //saves the image to a specified file location
    public void save(File loc)
    {
	   try {
		   ImageIO.write(img, "png", loc);

		   System.out.println("saved");
	   } catch (Exception ee) {
	   		ee.printStackTrace();

	   }
    }

	//returns the width of the image
    public int getWidth()
   {
   	return img.getWidth();
   }

   	//returns the height of the image
    public int getHeight()
   {
   	return img.getHeight();
   }
}