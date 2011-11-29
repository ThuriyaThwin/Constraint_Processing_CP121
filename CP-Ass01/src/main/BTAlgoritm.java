package main;

import java.util.Vector;

import algoritm.CSPAlgorithm;

public class BTAlgoritm extends CSPAlgorithm {

	@Override
	public int label(int i) {
		
		_consistent = false;
		
		Vector<Integer> tmpCurrentDomain = new Vector<Integer>();
		
		int size = _currentDomain.get(i).size();

		for  (int j = 0; j < size && !_consistent; j++){

			_consistent = true;

			_problem.getV().set(i, _currentDomain.get(i).get(j));

			for (int h = 0; h < i && _consistent; h++)
				_consistent = _problem.check(i, _problem.getV().get(i), h, _problem.getV().get(h));

			if (_consistent) tmpCurrentDomain.addElement(_currentDomain.get(i).get(j));
		}

		_currentDomain.set(i, tmpCurrentDomain);

		return (_consistent) ? i + 1 : i;
	}

	@Override
	public int unlabel(int i) {

		int h = i - 1;
		
		_currentDomain.set(i, _problem.getDomain().get(i));
		_currentDomain.get(h).remove(new Integer(_problem.getV().get(h)));
		
		_consistent = !_currentDomain.get(h).isEmpty();
		
		return h;
	}
}
