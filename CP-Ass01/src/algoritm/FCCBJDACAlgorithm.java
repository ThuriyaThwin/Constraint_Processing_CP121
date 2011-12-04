package algoritm;

import java.util.Vector;

import problem.Problem;

public class FCCBJDACAlgorithm extends FCCBJAlgorithm {

	protected	Vector<Vector<Integer>>	_dac;
	
	public FCCBJDACAlgorithm() {
		super();
	}

	public FCCBJDACAlgorithm(Problem problem) {
		super(problem);
	}
	
	@Override
	protected void init(Problem problem) {

		super.init(problem);

		_dac = new Vector<Vector<Integer>>(_problem.getN());
		
		for (int i = 0; i < _problem.getN(); i++)
			_dac.add(new Vector<Integer>(_problem.getD()));
	}
	
	@Override
	protected void labelExtansion(int k) {
		
		for (int p = _problem.getN() -1; p > k; k--){
			
			for (Integer Vp: _currentDomain.get(p)){	//TODO: change it...
			
				for (int q = p + 1; q < _problem.getN(); q++){
					
					boolean found = false;
					
					for (Integer Vq: _currentDomain.get(q)){
	
						if (_problem.check(p, Vp, q, Vq))
							found  = true;
					}
					
					if (!found)
						_currentDomain.get(p).remove(Vp);
				}
			}
		}
	}
}
