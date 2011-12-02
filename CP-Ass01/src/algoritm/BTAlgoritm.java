package algoritm;

import java.util.Vector;


public class BTAlgoritm extends CSPAlgorithm {

	@Override
	public int label(int i) {
		
		_consistent = false;
		
		while  (!_currentDomain.get(i).isEmpty() && !_consistent){

			_consistent = true;

			_problem.setVi(i, _currentDomain.get(i).firstElement());

			for (int h = 0; h < i && _consistent; h++)
				_consistent = _problem.check(i, _problem.getV().get(i), h, _problem.getV().get(h));

			if (!_consistent) //	tmpCurrentDomain.addElement(_currentDomain.get(i).get(j));
				_currentDomain.get(i).removeElementAt(0);//hard coded
		}

		return (_consistent) ? i + 1 : i;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int unlabel(int i) {

		int h = i - 1;
		
		if (-1 != h){ 
		
			_currentDomain.set(i, (Vector<Integer>)_problem.getDomain().get(i).clone());
			_currentDomain.get(h).remove(_problem.getV().get(h));
			
			_consistent = !_currentDomain.get(h).isEmpty();
		}
		
		return h;
	}
}
