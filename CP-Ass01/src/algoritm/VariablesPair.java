package algoritm;

public class VariablesPair {
	
	protected	int	_first;
	protected	int	_second;

	public VariablesPair(int first, int second) {
		setFirst(first);
		setSecond(second);
	}

	public void setSecond(int _second) {
		this._second = _second;
	}

	public int getSecond() {
		return _second;
	}

	public void setFirst(int _first) {
		this._first = _first;
	}

	public int getFirst() {
		return _first;
	}
}
