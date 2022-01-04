package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ESACore {

	public File[] documents;

	public HashMap<String, HashMap<String, Double>> invertedIndex = new HashMap<String, HashMap<String, Double>>();

	private HashMap<String, Integer> docSize = new HashMap<String, Integer>();

	public ESACore(String dirPath) {
		documents = new File(dirPath).listFiles();
		// System.out.println(documents.length);
	}

	private void populateDocSize() throws IOException {
		for (File document : documents) {
			String str = Utils.readFile(document.getAbsolutePath());
			StringTokenizer strTok = new StringTokenizer(str);
			int count = 0;
			while (strTok.hasMoreTokens()) {
				strTok.nextToken();
				count++;
			}
			docSize.put(document.getAbsolutePath(), count);
		}
	}

	@SuppressWarnings("unchecked")
	public void populateInvertedIndex() throws IOException,
			ClassNotFoundException {
		File invIndexFile = new File("./resources/invIndex.ser");
		if (!invIndexFile.exists()) {
			Utils.initStopWords();
			populateDocSize();
			for (File document : documents) {
				String documentPath = document.getAbsolutePath();
				StringTokenizer strTok = new StringTokenizer(
						Utils.readFile(documentPath));
				while (strTok.hasMoreTokens()) {
					String token = strTok.nextToken().toLowerCase();
					token = token.replaceAll("[^a-z0-9A-Z]", "");
					if (Utils.stopwords.contains(token) || token.length() < 2)
						continue;
					if (!invertedIndex.containsKey(token)) {
						HashMap<String, Double> tfMap = new HashMap<String, Double>();
						tfMap.put(documentPath, 1.0 / docSize.get(documentPath));
						invertedIndex.put(token, tfMap);
					} else if (!invertedIndex.get(token).containsKey(
							documentPath)) {
						HashMap<String, Double> tfMap = invertedIndex
								.get(token);
						tfMap.put(documentPath, 1.0 / docSize.get(documentPath));
					} else {
						HashMap<String, Double> tfMap = invertedIndex
								.get(token);
						Double tf = tfMap.get(documentPath);
						tf += 1.0 / docSize.get(documentPath);
						tfMap.put(documentPath, tf);
					}
				}
			}
			FileOutputStream fileOut = new FileOutputStream(
					"./resources/invIndex.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(invertedIndex);
			out.close();
			fileOut.close();
		} else {
			FileInputStream fileIn = new FileInputStream(
					"./resources/invIndex.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			invertedIndex = (HashMap<String, HashMap<String, Double>>) in
					.readObject();
			in.close();
			fileIn.close();
		}
	}

	public ArrayList<Double> getVector(String str) {
		StringTokenizer strTok = new StringTokenizer(str);
		ArrayList<Double> vector = new ArrayList<Double>();
		for (int i = 0; i < documents.length; i++) {
			vector.add(0.0);
		}
		while (strTok.hasMoreTokens()) {
			String token = strTok.nextToken();
			HashMap<String, Double> tfMap = invertedIndex.get(token
					.toLowerCase());
			if (tfMap == null)
				continue;
			double idf = (double)documents.length / (double)invertedIndex.get(token).size();
			for (int i = 0; i < documents.length; i++) {
				if (tfMap.containsKey(documents[i].getAbsolutePath()))
					vector.set(
							i,
							vector.get(i)
									+ tfMap.get(documents[i].getAbsolutePath())
									* idf);
			}
		}
		return vector;
	}

//	public double getSimilarity(String srt1, String str2) {
//		ArrayList<Double> vector1 = getVector(srt1);
//		ArrayList<Double> vector2 = getVector(str2);
//		;
//		if (Utils.getDotProduct(vector1, vector2) == 0)
//			return 0.0;
//		else {
//			double similarity = Utils.getDotProduct(vector1, vector2)
//					/ Math.sqrt(Utils.getDotProduct(vector1, vector1)
//							* Utils.getDotProduct(vector2, vector2));
//			return similarity;
//		}
//	}
}
