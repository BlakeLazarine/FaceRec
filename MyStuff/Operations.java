package MyStuff;//Written by Blake Lazarine
//This class contains static methods to use is other parts of the program. They deal mostly with vector operations

import java.awt.image.BufferedImage;
import MathStuff.*;

public class Operations {

    //Only gets the first n columns of the U matrix produced by the singular value decomposition of an m by n matrix
    public static Matrix getFirstNColsOfU(Matrix mat) {
        SingularValueDecomposition s = new SingularValueDecomposition(mat);
        Matrix V = s.getV();
        Matrix e = mat.times(V);

        return unitCols(e);
    }


    public static double[] avgVec(double[][] vecs){
        double[] result = new double[vecs[0].length];
        for(int i = 0; i < vecs[0].length; i++)
        {
            result[i] = 0;
            for(int j = 0; j < vecs.length; j++)
            {
                result[i]+=vecs[j][i];
            }
            result[i] /= vecs.length;
        }
        return result;
    }

    //returns the single-dimensional array representation of a matrix
    public static double[] toArray(Matrix mat) {
        double[] singleDim = new double[mat.getColumnDimension() * mat.getRowDimension()];
        int count = 0;
        double[][] arr = mat.getArray();
        for (int i = 0; i < mat.getRowDimension(); i++) {
            for (int j = 0; j < mat.getColumnDimension(); j++) {
                singleDim[count] = arr[i][j];
                count++;
            }
        }
        return singleDim;
    }

    //subtracts an array vector from every row of a martix. This modifies the matrix. This method is used for subtracting the mean face from every face in the training set
    public static void subtractArrFromRow(Matrix mat, double[] mean)
    {
    	double[][] A = mat.getArray();
    	for(int i = 0; i < A.length; i++)
    	{
    		for(int j = 0; j < A[0].length; j++)
    		{
    			A[i][j] = A[i][j] - mean[j];
    		}
    	}
    }

    //subtracts one array from another. modifies the first array
    public static void subtractArrs(double[] a1, double[] a2)
    {
        for(int i = 0; i < a1.length; i++)
        {
            a1[i] -= a2[i];
        }
    }

    //returns the difference vector between two vectors. Neither input is modified
    public static double[] diffArrs(double[] a1, double[] a2)
    {
    	double[] result = new double[a1.length];
        for(int i = 0; i < a1.length; i++)
        {
            result[i] = a1[i] - a2[i];
        }
        return result;
    }

    //adds one array to another, modifying the original array
    public static void addArrs(double[] a1, double[] a2)
    {
        for(int i = 0; i < a1.length; i++)
        {
            a1[i] += a2[i];
        }
    }

    //returns a new array in which a vector is multiplied by a scalar value
    public static double[] arrTimes(double[] arr, double x)
    {
        double[] result = new double[arr.length];
        for(int i = 0; i < arr.length; i++)
        {
            result[i] = arr[i] * x;
        }
        return result;
    }



    //Used to create saved images for eigenfaces. Amplifies the effect and changes average value for visibility
    public static double[][] multiplyAndAddConstants(double[][] A, int x, int n)
    {
        double[][] B = new double[A.length][A[0].length];
        for(int i = 0; i < A.length; i++)
        {
            for(int j = 0; j < A[0].length; j++)
            {
                B[i][j] = x * A[i][j] + n;
            }
        }
        return B;
    }



    //returns the average value of each position in an array of matrices
    public static Matrix avgMat(Matrix[] mats) {
        int rows = mats[0].getRowDimension();
        int cols = mats[0].getColumnDimension();
        Matrix sum = new Matrix(rows, cols);
        for (int idx = 0; idx < mats.length; idx++) {
            sum.plusEquals(mats[idx]);
        }
        return sum.times(1 / ((double) mats.length));
    }

    //returns the average value of every value in a 1-dimensional array
    public static double[] avgArr(double[][] arr) {
        double[] avg = new double[arr[0].length];
        for (int idx = 0; idx < arr[0].length; idx++) {
            avg[idx] = 0;
            for (int i = 0; i < arr.length; i++) {
                avg[idx] += arr[i][idx];
            }
            avg[idx] /= arr.length;
        }
        return avg;
    }

    //returns the unit vectors of the columns of a matrix as the columns of a new matrix
    public static Matrix unitCols(Matrix mat) {
        Matrix T = mat.transpose();
        double vals[][] = T.getArray();
        for (int i = 0; i < vals.length; i++) {
            vals[i] = unitVec(vals[i]);
        }
        return T.transpose();
    }

    //returns the unit vector in the same direction as a passed vector
    public static double[] unitVec(double[] arr) {
        double mag = Math.sqrt(dotProd(arr, arr));
        double[] u = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
            u[i] = arr[i] / (mag);
        return u;
    }

    //returns the dot product of two vectors
    public static double dotProd(double[] arr1, double[] arr2) {

        double sum = 0;
        try {
            for (int i = 0; i < arr1.length; i++) {
                sum += arr1[i] * arr2[i];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Vectors in Dot Product not of same dim");
        }

        return sum;
    }




}