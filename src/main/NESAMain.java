package main;

import java.io.IOException;

public class NESAMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		String word = "perceptron";
		ESACore esaCore = new ESACore("/home/ubuntu/Downloads/machine_learning");
		esaCore.populateInvertedIndex();
		ESAWikiTitle esaWikiTitle = new ESAWikiTitle(esaCore.invertedIndex, esaCore.documents);
		esaWikiTitle.initWikiTitle();
		ESAWikiBody esaWikiBody = new ESAWikiBody(esaCore.invertedIndex, esaCore.documents);
		esaWikiBody.initWikiBody();
		System.out.println(esaCore.getVector(word));
		System.out.println(esaWikiTitle.getVector(word));
		System.out.println(esaWikiBody.getVector(word));
	}
	
}
