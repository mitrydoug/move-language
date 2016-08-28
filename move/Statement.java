package move;
//STATEMENT
//Move Statement
//print statement
//assignment statement

public class Statement implements MoveStructure{

	private static final int MOVE_STATEMENT = 0;
	private static final int PRINT_STATEMENT = 1;
	private static final int ASSIGNMENT_STATEMENT = 2;
	private static final int UNDETERMINED = 3;

	private int statementType;

	private MoveIdentifier identifier; // for assignement statment
	private Expression exp;       // for all three statement types

	public Statement(){
		statementType = UNDETERMINED;
		identifier = null;
		exp = null;
	}

	public Statement(Statement s){           //copy constructor
		this.statementType = s.statementType;
		if(s.identifier == null){
			this.identifier = null;
		}else{
			this.identifier = new MoveIdentifier(s.identifier);
		}

		if(s.exp == null){
			this.exp = null;
		}else{
			this.exp = new Expression(s.exp);
		}
	}

	public void execute() throws IllegalStateException{
		if(statementType == UNDETERMINED){
			throw new IllegalStateException("This statement has not been built");
		}else if(statementType == MOVE_STATEMENT){
			if(exp.evaluate() - (int)exp.evaluate() > 0.00000000001 || exp.evaluate() - (int)exp.evaluate() < -0.00000000001){
				throw new IllegalArgumentException("Cannot Move fractional lines");
			}
			int newLine = MoveSystem.getLine() + (int)exp.evaluate();
			MoveSystem.setLine(newLine);
		} else if(statementType == PRINT_STATEMENT){
			MoveSystem.println(""+exp.evaluate());
			MoveSystem.setLine(MoveSystem.getLine()+1);
		} else if(statementType == ASSIGNMENT_STATEMENT){
			MoveSystem.assign(exp.evaluate(), identifier.getName());
			MoveSystem.setLine(MoveSystem.getLine()+1);
		}
	}

	public MoveElement[][] getExpansions(){
		MoveElement[][] expansions = new MoveElement[3][];
		MoveElement[] exp0 = {new MoveIdentifier(), MoveToken.EQUALS_TOKEN, new Expression()};
		expansions[0] = exp0;
		MoveElement[] exp1 = {MoveToken.MOVE_TOKEN, new Expression()};
		expansions[1] = exp1;
		MoveElement[] exp2 = {MoveToken.PRINT_TOKEN, new Expression()};
		expansions[2] = exp2;
		return expansions;
	}

	public void build(MoveElement[] els) throws IllegalArgumentException{
		if(els.length == 2){ //either move or print
			if(((MoveToken)els[0]).matches(MoveToken.MOVE_TOKEN)){
				statementType = MOVE_STATEMENT;
				exp = (Expression)els[1];
			}else if(((MoveToken)els[0]).matches(MoveToken.PRINT_TOKEN)){
				statementType = PRINT_STATEMENT;
				exp = (Expression)els[1];
			}
		} else if(els.length == 3){ //assignment
			if(((MoveToken)els[1]).matches(MoveToken.EQUALS_TOKEN)){
				statementType = ASSIGNMENT_STATEMENT;
				identifier = (MoveIdentifier)els[0];
				exp = (Expression)els[2];
			} else {
				throw new IllegalStateException("assignment statement 2nd term is " + els[1]);
			}
		} else {
			throw new IllegalStateException("Length of Statement expansion is" + els.length);
		}
	}

	public Expression getExpression(){
		return exp;
	}

	public String toString(){
		if(statementType == UNDETERMINED){
			return "STMT UND";
		} else if(statementType == MOVE_STATEMENT){
			return "MOVE<" + exp + ">";
		} else if(statementType == PRINT_STATEMENT){
			return "PRINT<" + exp + ">";
		} else if(statementType == ASSIGNMENT_STATEMENT){
			return identifier + " = " + exp;
		}
		return ""; // won't reach this
	}

	public boolean equals(Statement s){
		if(statementType == s.statementType && exp != null && s.exp != null && exp.equals(s.exp)){
			return true;
		}else{
			return false;
		}
	}
}