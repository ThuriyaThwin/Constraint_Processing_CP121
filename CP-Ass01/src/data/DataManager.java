package data;

import java.util.Map;
import java.util.Vector;

import problem.Problem;

import com.thoughtworks.xstream.XStream;

public class DataManager {

	protected	XStream	_xstream;
	
	public DataManager() {
	
		_xstream = new XStream();
		
		_xstream.alias("problem", Problem.class);
		_xstream.alias("vector", Vector.class);
		_xstream.alias("map", Map.class);
	}
	
	public void storeProblems(Vector<Problem> problem, String fileName){
		
		//	String xml = _xstream.toXML(joe);
	}
	
	public Vector<Problem> restoreProblems(String fileName){
		
		//	Vector<Problem> problems = (Vector<Problem>)xstream.fromXML(xml);
		
		return null;
	}
	
}
