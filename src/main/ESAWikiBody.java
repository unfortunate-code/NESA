package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class ESAWikiBody {

	private HashMap<String, HashMap<String, Double>> invertedIndex;

	private double[][] transformationMatrix;

	private double[][] correlationMatrix;
	
	private File[] concepts;

	public ESAWikiBody(HashMap<String, HashMap<String, Double>> invertedIndex,
			File[] concepts) {
		this.invertedIndex = invertedIndex;
		this.concepts = concepts;
		transformationMatrix = new double[concepts.length][concepts.length];
		correlationMatrix = new double[concepts.length][concepts.length];
	}

	public HashMap<String, HashMap<String, Double>> wikiBodyIndex = new HashMap<String, HashMap<String, Double>>();

	public void initWikiBody() throws IOException, ClassNotFoundException {
		BufferedReader buffer = new BufferedReader(new FileReader(
				"./resources/hyperLinkTranformation.txt"));

		String line;
		int row = 0;
		int size = 0;

		while ((line = buffer.readLine()) != null) {
			String[] vals = line.trim().split("\\s+");

			// Lazy instantiation.
			if (transformationMatrix == null) {
				size = vals.length;
				System.out.println("size   " + size);
				transformationMatrix = new double[size][size];
			}

			for (int col = 0; col < size; col++) {
				transformationMatrix[row][col] = Integer.parseInt(vals[col]);
			}

			row++;
		}
		System.out.println(row + "   " + size);
		buffer.close();
	         correlationMatrix = squareMatrixMultiplication(transformationMatrix, squareTranspose(transformationMatrix));
		}
	}
	
	private static double[][] squareMatrixMultiplication(double[][] matrix1,double[][] matrix2){
		int size = matrix1.length;
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
	
	private double[][] squareTranspose(double[][] matrix){
		int size = matrix.length;
		double[][] resultMatrix = new double[size][size];
		for(int i = 0;i < size;i++){
			for(int j = 0;j < size;j++){
				resultMatrix[i][j] = matrix[j][i];
			}
		}
		return resultMatrix;
	}

	private void initTransformationMatrix() {
		int i = 0, j = 0;
		for (Entry<String, HashMap<String, Double>> conceptEntry : wikiBodyIndex
				.entrySet()) {
			j = 0;
			for (Entry<String, Double> conceptVectorEntry : conceptEntry
					.getValue().entrySet()) {
				transformationMatrix[i][j] = conceptVectorEntry.getValue();
				j++;
			}
			i++;
		}
		correlationMatrix = squareMatrixMultiplication(transformationMatrix, squareTranspose(transformationMatrix));
	}

	public ArrayList<Double> getVector(String str) {
		StringTokenizer strTok = new StringTokenizer(str);
		ArrayList<Double> vector = new ArrayList<Double>();
		for (int i = 0; i < concepts.length; i++) {
			vector.add(0.0);
		}
		while (strTok.hasMoreTokens()) {
			String token = strTok.nextToken();
			HashMap<String, Double> tfMap = invertedIndex.get(token
					.toLowerCase());
			if (tfMap == null)
				continue;
			double idf = (double)concepts.length / (double)invertedIndex.get(token).size();
			for (int i = 0; i < concepts.length; i++) {
				if (tfMap.containsKey(concepts[i].getAbsolutePath()))
					vector.set(
							i,
							vector.get(i)
									+ tfMap.get(concepts[i].getAbsolutePath())
									* idf);
			}
		}
		ArrayList<Double> transformedVector = new ArrayList<Double>();
		for (int i = 0; i < correlationMatrix.length; i++) {
			double temp = 0.0;
			for (int j = 0; j < vector.size(); j++) {
				temp += vector.get(j) * correlationMatrix[j][i];
			}
			transformedVector.add(temp);
		}
		return transformedVector;
	}
	
//	public double getSimilarity(String str1, String str2) {
//		ArrayList<Double> vector1 = getVector(str1);
//		ArrayList<Double> vector2 = getVector(str2);
//		if (Utils.getDotProduct(vector1, vector2) == 0)
//			return 0.0;
//		else {
//			double similarity = Utils.getDotProduct(vector1,
//					vector2)
//					/ Math.sqrt(Utils.getDotProduct(vector1,
//							vector1)
//							* Utils.getDotProduct(vector2,
//									vector2));
//			return similarity;
//		}
//	}
}
