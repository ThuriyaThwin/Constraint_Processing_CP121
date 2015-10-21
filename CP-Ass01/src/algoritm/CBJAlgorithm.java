package algoritm;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import problem.Problem;

public class CBJAlgorithm extends BTAlgorithm {

	protected	Vector<SortedSet<Integer>>	_confSets;
	
	public CBJAlgorithm() {
		super();
	}
	
	public CBJAlgorithm(Problem problem) {
		super(problem);
	}
	
	@Override
	protected void init(Problem problem) {
		
		super.init(problem);
		
		_confSets = new Vector<SortedSet<Integer>>();
		
		for (int i = 0; i < _problem.getN(); i++)
			_confSets.add(new TreeSet<Integer>());
	}
	

	
	@Override
	protected void doSomethingLabel(int hMinusOne, int i) {
		
		_confSets.get(i).add(new Integer(hMinusOne));
	}
	
	@Override
	protected int getHFromI(int i) {
		
		if (_confSets.get(i).isEmpty())
			return -1;
		
		return _confSets.get(i).last();
	}
	
	@Override
	protected void doSomethingUnlabel(int h, int i) {
		
		_confSets.get(i).remove(_confSets.get(i).last());	// removes h
		_confSets.get(h).addAll(_confSets.get(i));

		for (int j = h + 1; j <= i; j++){
			
			_confSets.get(j).clear();
			super.restoreCurrentDomain(j);
		}
	}
}
