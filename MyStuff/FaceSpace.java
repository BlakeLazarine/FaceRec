package MyStuff;

import java.awt.image.BufferedImage;

import java.io.File;
import MathStuff.*;
public class FaceSpace {

    private double[][] eigenFaces;
    
    private File saveLoc;
    private double[] meanFace;
	//builds the face space from training set of images
    public FaceSpace(BufferedImage[] trainingSet)
    {
    	ResizableImage firstImg = new ResizableImage(trainingSet[0]);
    	double[] img0 = firstImg.getVector();
    	double[][] A = new double[trainingSet.length][img0.length];
    	A[0] = img0;
    	System.out.println("length = " + A[0].length);
    	//gets the images in vector form and enters them into matrix A
    	for(int i = 1; i < trainingSet.length; i++)
    	{
    		A[i] = new ResizableImage(trainingSet[i]).getVector();
    		System.out.println("length = " + A[i].length);
    	}
    	System.out.println("Done building A Double[][]");
    	
    	//creates a folder called FaceSpace
    	saveLoc = new File(FaceRecRun.trainingLoc.toString().substring(0, FaceRecRun.trainingLoc.toString().lastIndexOf('\\') + 1) + "FaceSpace");
        if (!saveLoc.exists()) {
        	saveLoc.mkdir();
        }
    	
    	//Finds and saves average face
    	meanFace = Operations.avgArr(A);
    	ResizableImage mean = new ResizableImage(firstImg.getWidth(), firstImg.getHeight(), meanFace);
    	mean.save(new File(saveLoc, "mean.png"));
    	
    	
    	Matrix mat  = new Matrix(A);
    	Operations.subtractArrFromRow(mat, meanFace);
    	System.out.println("Done building MathStuff.Matrix");


    	//gets a 2-dim array with rows being eigenfaces
    	Matrix eigs = Operations.getFirstNColsOfU(mat.transpose()).transpose();
    	
    	System.out.println("Done with SVD");
		eigenFaces = eigs.getArray();

		//converts eigenFaces into form for easy viewing
		double[][] printableFaces = Operations.multiplyAndAddConstants(eigenFaces, 3000, 127);
		System.out.println("length = " + eigenFaces[0].length);

		//saves eigenfaces as images
		for(int i = 0; i < A.length; i++)
		{
			(new ResizableImage(firstImg.getWidth(), firstImg.getHeight(), printableFaces[i])).save(new File(saveLoc, i + ".png"));
		}
    }
	
	//unused in code; for testing only
	public FaceSpace(BufferedImage[] trainingSet, boolean test)
	{
		eigenFaces = new double[trainingSet[0].getHeight() * trainingSet[0].getWidth()][trainingSet.length];
		ResizableImage firstImg = new ResizableImage(trainingSet[0]);
		double[] img0 = firstImg.getVector();
		double[][] A = new double[trainingSet.length][img0.length];
		A[0] = img0;
		System.out.println("length = " + A[0].length);
		for(int i = 1; i < trainingSet.length; i++)
		{
			A[i] = new ResizableImage(trainingSet[i]).getVector();
			System.out.println("length = " + A[i].length);
		}
		System.out.println("Done building A Double[][]");

		saveLoc = new File(FaceRecRun.trainingLoc + "FaceSpace");
		if (!saveLoc.exists()) {
			saveLoc.mkdir();
		}

		meanFace = Operations.avgArr(A);

		for(int i = 0; i < trainingSet.length; i++)
		{
			eigenFaces[i] = Operations.diffArrs(A[i], meanFace);
		}
	}

	//Finds the weight vector by projecting the image into face space
	public double[] projectImg(BufferedImage img)
	{
		ResizableImage ReImg = new ResizableImage(img);
		double[] vec = ReImg.getVector();
		Operations.subtractArrs(vec, meanFace);

		double[] weight = new double[eigenFaces.length];
		for(int i = 0; i < eigenFaces.length; i++)
		{
			weight[i] = Operations.dotProd(eigenFaces[i], vec);
		}


		return weight;
	}
	
	//projects an input image into the face space, returning a weight vector representing its position
	public double[] projectImg(ResizableImage ReImg)
	{
		double[] vec = ReImg.getVector();
		Operations.subtractArrs(vec, meanFace);

		double[] weight = new double[eigenFaces.length];
		for(int i = 0; i < eigenFaces.length; i++)
		{
			weight[i] = Operations.dotProd(eigenFaces[i], vec);
		}


		return weight;
	}
	
	//recontructs an image from a weight vector
	public ResizableImage reconstruction(double[] weight, int w, int h)
	{
		double[] result = new double[eigenFaces[0].length];
		for(int i  = 0; i < weight.length; i++)
		{
			Operations.addArrs(result, Operations.arrTimes(eigenFaces[i], weight[i]));
		}
		Operations.addArrs(result, meanFace);

		return new ResizableImage(w, h, result);
	}

}
