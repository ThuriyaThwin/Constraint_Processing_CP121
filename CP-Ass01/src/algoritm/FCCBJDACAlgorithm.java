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
		for(int i = 0;i <_problem.getN();i++){
			for (int j = 0; j < _problem.getD(); j++) {
				_dac.get(i).add(0);
			}
		}
	}

	@Override
	protected boolean labelExtansion(int k) {

		boolean tAns = true;
		
		Vector<Vector<Integer>> tValuesToRemove = new Vector<Vector<Integer>>();
				
		for (int p = _problem.getN() - 1; p > k; p--){

			Vector<Integer> tIndicesVec = new Vector<Integer>();
			Vector<Integer> tVec = new Vector<Integer>();
			
			for (int tL = 0; tL < _currentDomain.get(p).size(); tL++){	//TODO: change it...
			
				Integer Vp = _currentDomain.get(p).get(tL);
				
				for (int q = p + 1; q < _problem.getN(); q++){

					boolean tFound = false;

					for (Integer Vq: _currentDomain.get(q)){

						if (_problem.check(p, Vp, q, Vq))//TODO:Problem here i-j=true?
							tFound  = true;
					}

					if (!tFound){
				
						tIndicesVec.add(tL);//Saving the indices which we will dissapper later, in order to update _dac
						tVec.add(Vp);
						
						break; //TODO:Hard coded,change this?
					}
				}
			}
			
			updateDAC(p, tIndicesVec, k);
			
			//clearCurrentDomain(p, tIndicesVec);
			
			_currentDomain.get(p).removeAll(tVec);
			
			tValuesToRemove.add(tVec);
			
			if (_currentDomain.get(p).isEmpty()){
				
				for (int l = 0; l < tValuesToRemove.size(); l++){
					
					_currentDomain.get(p+l).addAll(tValuesToRemove.get(l));
				}
				
				tAns = false;
				break;
			}
		}
		
		return tAns;
	}

	
	//TODO:Check this, very weird.
	@Override
	protected void unlabelExtansion(int i,int h){
		
		Vector<Integer> tbuildingDomain = (Vector<Integer>)_currentDomain.get(i).clone();
		int tCounter = 0;
		for(int j = 0;j < _dac.get(i).size();j++){
			//Restore from original
			if(_dac.get(i).get(j) >= h){
				tbuildingDomain.add(j + tCounter, _problem.getDomain().get(i).get(j));
				tCounter++;
			}
			else if(_dac.get(i).get(j) == 0){
			}
		}
	}
	/**
	 * Use this function,tells us if we have emptied a current domain.
	 * @param i = the index to the domain we want to clear.
	 * @param IndicesVec
	 * @return
	 */
	private boolean clearCurrentDomain(int i, Vector<Integer> IndicesVec) {

		Vector<Integer> tAns = new Vector<Integer>();

		for (int j = 0; j < _currentDomain.get(i).size(); j++)
			
			if(!IndicesVec.contains(j)){
				tAns.add(_currentDomain.get(i).get(j));
		}
		
		if(tAns.size() != _currentDomain.get(i).size())
			_currentDomain.set(i, tAns);//nice Replace?

		return _currentDomain.get(i).isEmpty();
	}

	/**
	 * This function updates the Row i in the _dac matrix
	 * @param i - Row to update in matrix _dac
	 * @param indices - a Vector of indices which were removed from the current domain due 
	 * to the DAC Procedure
	 * @param h - Is the index of the varible after it's assignment we ran DAC procedure.
	 */
	private void updateDAC(int i, Vector<Integer> indices, int h){
		
		for(int l = 0; l < _problem.getD(); l++){
		
			if(indices.contains(l))
				_dac.get(i).set(l, h);

			else
				_dac.get(i).set(l, 0);
		}
	}
}
