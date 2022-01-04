package main;

public class Test {
	
	public static void main(String[] args){
		double[][] matrix1 = new double[2][2];
		double[][] matrix2 = new double[2][2];
		for(int i = 0;i < 2;i++){
			for(int j = 0;j < 2;j++){
				matrix1[i][j] = i+j;
				matrix2[i][j] = i+2*j;
			}
		}
		double[][] matrix3 = squareMatrixMultiplication(matrix1,matrix2,2);
		for(int i = 0;i < 2;i++){
			for(int j = 0;j < 2;j++){
				System.out.print(matrix3[i][j] + " ");
			}
			System.out.println();
		}
		double[][] matrix4 = matrix3;
		for(int i = 0;i < 2;i++){
			for(int j = 0;j < 2;j++){
				System.out.print(matrix4[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	private static double[][] squareMatrixMultiplication(double[][] matrix1,double[][] matrix2,int size){
		double[][] resultMatrix = new double[size][size];
		
		for(int i = 0;i < size;i++){
			for(int j = 0;j < size;j++){
				resultMatrix[i][j] = 0.0;
			}
		}
		
		for(int i = 0;i < size;i++){
			for(int j = 0;j < size;j++){
				for(int k = 0;k < size;k++){
					resultMatrix[i][j] += matrix1[i][k]*matrix2[k][j];
				}
			}
		}
		return resultMatrix;
	}
}
