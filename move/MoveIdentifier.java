package move;
public class MoveIdentifier extends MoveToken{
	private boolean determined;
	private String value;

	public MoveIdentifier(String val){
		super(MoveToken.IDENTIFIER);
		value = val;
		determined = true;
	}

	public MoveIdentifier(){
		super(MoveToken.IDENTIFIER);
		determined = false;
	}

	public MoveIdentifier(MoveIdentifier m){
		super(MoveToken.IDENTIFIER);
		this.value = m.value;
		this.determined = m.determined;
	}

	public String getName() throws IllegalStateException {
		if(!determined) throw new IllegalStateException();
		else return value;
	}

	public String toString() throws IllegalStateException{
		if(!determined){
			return "IDT UND";
		} else {
			return value;
		}
	}

	public void setValue(String val){
		value = val;
		determined = true;
	}

	public boolean equals(MoveIdentifier i){
		return value.equals(i.value);
	}
}