package MyStuff;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class FaceRecRun
{
	public static BufferedImage[] training;
	public static File trainingLoc;
	public static double[][] weights;
	public static String[] names;
	public static FaceSpace F;

	public static OpenWebcam w;

	//Iterations is used in the WebcamPanel Class to deal with real-time operations
	public static int iterations = 0;

	//captures an image from the webcam. The image is projected onto the face space and the weight vector is compared to those of the training set
	public static void project(){


	//finds weight vector
	ResizableImage img = new ResizableImage(w.getWebImg(), true);
	double[] weight = F.projectImg(img.getImg());



	//arraylists for determining order or similarity
	ArrayList<Double> orderedVals= new ArrayList<Double>(weights.length);
	ArrayList<Integer> orderedIdx= new ArrayList<Integer>(weights.length);

	System.out.println("Differences between known faces and input");

	for(int i = 0; i < weights.length; i++)
	{
		//finds magnitude of difference vector between the unit weight vectors
		double[] diff = Operations.diffArrs(//Operations.unitVec
				(weight), weights[i]);
		double mag = Operations.dotProd(diff, diff);
		System.out.println(mag + "    " + names[i]);
		boolean done = false;

		//determine the order of likeliness of match
		for(int j = 0; j < orderedVals.size(); j++)
		{
			if (orderedVals.get(j) > mag)
			{
				orderedVals.add(j, mag);
				orderedIdx.add(j, i);
				done = true;
				break;
			}
		}
		if(!done)
		{
			orderedVals.add(mag);
			orderedIdx.add(i);
		}
	}


	//sets the labels for the best 3 guesses
	w.firstGuess.setText(names[orderedIdx.get(0)] + " ");
	w.secondGuess.setText(names[orderedIdx.get(1)] + " ");
	w.thirdGuess.setText(names[orderedIdx.get(2)] + " ");
	w.setTitle("I think this is " + names[orderedIdx.get(0)]);


}
	

	//Captures image from the webcam, projects it onto the face space, and saves the image
	public static ActionListener reconstruct = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ResizableImage img = new ResizableImage(w.getWebImg(), true);
			double[] weight = F.projectImg(img.getImg());
			File saveLoc = new File(trainingLoc.getParent(),"reconstruction.png");
			ResizableImage projection = F.reconstruction(weight, training[0].getWidth(), training[0].getHeight());
			projection.save(saveLoc);
		}
	};

	public static ActionListener mergeFaces = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			File loc1, loc2;

			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				loc1 = jfc.getSelectedFile();
				JFileChooser jfc2 = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				jfc2.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnValue2 = jfc2.showOpenDialog(null);

				if (returnValue2 == JFileChooser.APPROVE_OPTION) {
					loc2 = jfc2.getSelectedFile();

					try{

						ResizableImage img1 = new ResizableImage(ImageIO.read(loc1));
						ResizableImage img2 = new ResizableImage(ImageIO.read(loc2));


						double[] weight1 = F.projectImg(img1.getImg());
						double[] weight2 = F.projectImg(img2.getImg());

						double[] avg = Operations.avgVec(new double[][] {weight1, weight2});

						File saveLoc = new File(trainingLoc.getParent(),"merge.png");
						ResizableImage projection = F.reconstruction(avg, training[0].getWidth(), training[0].getHeight());
						projection.save(saveLoc);

					}catch (Exception ee){

					}


				}
			}

		}
	};


	public static ActionListener recFromFile = new ActionListener() {
		public void actionPerformed(ActionEvent e) {



			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				try {
					System.out.println("recognizing from file\n\n\n\n\n\n\n\n\n\n");
					//finds weight vector
					ResizableImage img = new ResizableImage(ImageIO.read(jfc.getSelectedFile()));
					double[] weight = F.projectImg(img.getImg());


					//arraylists for determining order or similarity
					ArrayList<Double> orderedVals = new ArrayList<Double>(weights.length);
					ArrayList<Integer> orderedIdx = new ArrayList<Integer>(weights.length);

					System.out.println("Differences between known faces and input");

					for (int i = 0; i < weights.length; i++) {
						//finds magnitude of difference vector between the unit weight vectors
						double[] diff = Operations.diffArrs(//Operations.unitVec
								(weight), weights[i]);
						double mag = Operations.dotProd(diff, diff);
						System.out.println(mag + "    " + names[i]);
						boolean done = false;

						//determine the order of likeliness of match
						for (int j = 0; j < orderedVals.size(); j++) {
							if (orderedVals.get(j) > mag) {
								orderedVals.add(j, mag);
								orderedIdx.add(j, i);
								done = true;
								break;
							}
						}
						if (!done) {
							orderedVals.add(mag);
							orderedIdx.add(i);
						}
					}


					//sets the labels for the best 3 guesses
					w.firstGuess.setText(names[orderedIdx.get(0)] + " ");
					w.secondGuess.setText(names[orderedIdx.get(1)] + " ");
					w.thirdGuess.setText(names[orderedIdx.get(2)] + " ");
					w.setTitle("I think this is " + names[orderedIdx.get(0)]);
				} catch (Exception ee) {
				}
			}


		}
	};

	//creates the face space based on selected training set
	public static void buildSpace()
	{
		F = new FaceSpace(training);
		weights = new double[training.length][training.length];
		for(int i = 0; i < weights.length; i++)
		{
			weights[i] = //Operations.unitVec
					(F.projectImg(training[i]));

		}
		System.out.println("done making weights. length = " + weights.length);
	}

	//takes in selected normal photos
	public static ActionListener fromNormal = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			File trainingLocation;

			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				trainingLocation = jfc.getSelectedFile();
				System.out.println(trainingLocation.getAbsolutePath());
				File[] listOfFiles = trainingLocation.listFiles();
				File f = new File(trainingLocation.getParent(), "Cropped Faces");
				if (!f.exists()) {
					f.mkdir();
				}
				for(int i = 0; i < listOfFiles.length; i++)
				{
					try
					{
						ResizableImage img = new ResizableImage(ImageIO.read(listOfFiles[i]), true);

						img.save(new File(f.toString(), listOfFiles[i].getName()));

					} catch (Exception ee) {

					}
				}


				trainingLoc = f;
				System.out.println(f.getAbsolutePath());
				listOfFiles = f.listFiles();
				training = new BufferedImage[listOfFiles.length];
				names = new String[listOfFiles.length];
				for(int i = 0; i < listOfFiles.length; i++)
				{
					try
					{
						training[i] = ImageIO.read(listOfFiles[i]);
						names[i] = listOfFiles[i].getName().replaceFirst("[.][^.]+$", "");;

					} catch (Exception ee) {

					}
				}

				buildSpace();
			}


		}
	};

	//Uses file system to select folder with cropped images for training set
	public static ActionListener fromCropped = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			File trainingLocation;

			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jfc.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				trainingLocation = jfc.getSelectedFile();
				trainingLoc = trainingLocation;
				System.out.println(trainingLocation.getAbsolutePath());
				File[] listOfFiles = trainingLocation.listFiles();
				training = new BufferedImage[listOfFiles.length];
				names = new String[listOfFiles.length];
				for(int i = 0; i < listOfFiles.length; i++)
				{
					try
					{
						training[i] = ImageIO.read(listOfFiles[i]);
						names[i] = listOfFiles[i].getName().replaceFirst("[.][^.]+$", "");;

					} catch (Exception ee) {

					}
				}

				buildSpace();
			}
		}
	};


	public static void main(String[] args) {
		//Makes UI Windows-based instead of default Java
		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}
		w = new MyStuff.OpenWebcam();
		w.setVisible(true);
		w.setSize(500, 500);
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	}
}


