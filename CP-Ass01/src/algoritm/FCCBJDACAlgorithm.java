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
	protected void labelExtansion(int k) {

		for (int p = _problem.getN() -1; p > k; p--){

			//index for iterating - tL
			//int tCurrentDomainStartSize = _currentDomain.get(p).size();
			//int tRemovedCount = 0;
			Vector<Integer> tIndicesVec = new Vector<Integer>();
			for (int tL = 0;tL < _currentDomain.get(p).size();tL++){	//TODO: change it...
				Integer Vp = _currentDomain.get(p).get(tL);
				for (int q = p + 1; q < _problem.getN(); q++){

					boolean tFound = false;

					for (Integer Vq: _currentDomain.get(q)){

						if (_problem.check(p, Vp, q, Vq))//TODO:Problem here i-j=true?
							tFound  = true;
					}

					if (!tFound){
						tIndicesVec.add(tL);//Saving the indices which we will dissapper later, in order to update _dac
						//_currentDomain.get(p).remove(tL);
						//tL--;
						break;//TODO:Hard coded,change this?
						//_currentDomain.get(p).remove(Vp);
					}
				}
			}
			updateDAC(p, tIndicesVec, k);
			clearCurrentDomain(p,tIndicesVec);
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

		for (int j = 0; j < _currentDomain.get(i).size();j++) {
			if(!IndicesVec.contains(j)){
				tAns.add(_currentDomain.get(i).get(j));
			}
		}
		if(tAns.size() != _currentDomain.get(i).size()){
			_currentDomain.set(i,tAns);//nice Replace?
		}
		return _currentDomain.get(i).isEmpty();

	}

	/**
	 * This function updates the Row i in the _dac matrix
	 * @param i - Row to update in matrix _dac
	 * @param indices - a Vector of indices which were removed from the current domain due 
	 * to the DAC Procedure
	 * @param h - Is the index of the varible after it's assignment we ran DAC procedure.
	 */
	private void updateDAC(int i,Vector<Integer> indices,int h){
		for(int l = 0;l < _problem.getD();l++){
			if(indices.contains(l)){
				_dac.get(i).set(l, h);
			}
			else{
				_dac.get(i).set(l, 0);
			}
		}
	}
}
