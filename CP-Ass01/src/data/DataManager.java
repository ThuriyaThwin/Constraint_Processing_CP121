package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import problem.Problem;

import algoritm.VariablesPair;

import com.thoughtworks.xstream.XStream;

public class DataManager {

	protected	XStream	_xstream;
	
	public DataManager() {
	
		_xstream = new XStream();
		
		_xstream.alias("problem", Problem.class);
		_xstream.alias("vector", Vector.class);
		_xstream.alias("map", Map.class);
		_xstream.alias("hashmap", HashMap.class);
		_xstream.alias("boolean", Boolean.class);
		_xstream.alias("integer", Integer.class);
		_xstream.alias("variablespair", VariablesPair.class);
	}
	
	public void storeProblems(Vector<Problem> problems, String fileName){
		
		try {
			
			writeToFile(_xstream.toXML(problems), fileName);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Vector<Problem> restoreProblems(String fileName){
		
		Vector<Problem> problems = null;
		
		try {
			
			problems = (Vector<Problem>)_xstream.fromXML(readFileAsString(fileName));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return problems;
	}

	public  String readFileAsString(String filePath) throws java.io.IOException {

		StringBuffer fileData = new StringBuffer(1000);

		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[1024];

		int numRead = 0;
		
		while((numRead = reader.read(buf)) != -1){
			
			String readData = String.valueOf(buf, 0, numRead);

			fileData.append(readData);

			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	public void writeToFile(String str, String filename) throws FileNotFoundException {
		
		PrintWriter out = new PrintWriter(filename);
		
		out.append(str);

		out.close();
   }

}
