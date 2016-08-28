package move;
public class MoveNumber extends MoveToken{
	private boolean determined;
	private double value;

	public MoveNumber(double val){
		super(MoveToken.NUMBER);
		value = val;
		determined = true;
	}

	public MoveNumber(){
		super(MoveToken.NUMBER);
		determined = false;
	}

	public MoveNumber(MoveNumber m){
		super(MoveToken.NUMBER);
		this.value = m.value;
		this.determined = m.determined;
	}

	public double evaluate() throws IllegalStateException {
		if(!determined) throw new IllegalStateException();
		else return value;
	}

	public void setValue(double val){
		value = val;
		determined = true;
	}

	public String toString() throws IllegalStateException{
		if(!determined){
			return "NBR UND";
		} else {
			return String.valueOf(value);
		}
	}

	public boolean equals(MoveNumber num){
		return value == num.value;
	}
}