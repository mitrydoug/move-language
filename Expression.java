package move;
//Expression Rules
//Exp -> Number
//Exp -> -Exp
//Exp -> (Exp)
//EXP -> identifier
//Exp -> Exp + Exp
//Exp -> Exp - Exp
//Exp -> Exp * Exp
//Exp -> Exp / Exp
//Exp -> Exp % Exp
//Exp -> Exp ^ Exp
//Exp -> Exp > Exp
//Exp -> Exp < Exp
import java.util.*;

public class Expression implements MoveStructure, Comparable<Expression>{

	public static final int EXP_NUMBER       = 0;
	public static final int EXP_NEGATIVE     = 1;
	public static final int EXP_PARENTHESIS  = 2;
	public static final int EXP_ADD          = 3;
	public static final int EXP_SUBTRACT     = 4;
	public static final int EXP_MULTIPLY     = 5;
	public static final int EXP_DIVIDE       = 6;
	public static final int EXP_REMAINDER    = 7;
	public static final int EXP_EXPONENT     = 8;
	public static final int EXP_GREATER_THAN = 9;
	public static final int EXP_LESS_THAN    = 10;
	public static final int EXP_UNDETERMINED = 11;
	public static final int EXP_IDENTIFIER   = 12;

	private int expressionType;

	private Expression exp1;
	private Expression exp2;
	private MoveNumber number;
	private MoveIdentifier identifier;

	public Expression(){
		expressionType = EXP_UNDETERMINED;
	}

	public Expression(Expression e){
		if(e.expressionType == EXP_UNDETERMINED){
			expressionType = EXP_UNDETERMINED;
			exp1 = exp2 = null;
		} else if(e.expressionType == EXP_NUMBER){
			this.expressionType = EXP_NUMBER;
			this.number = new MoveNumber(e.number);
		} else if(e.expressionType == EXP_IDENTIFIER){
			this.expressionType = EXP_IDENTIFIER;
			this.identifier = new MoveIdentifier(e.identifier);
		}else if(e.expressionType == EXP_NEGATIVE || e.expressionType == EXP_PARENTHESIS){
			this.expressionType = e.expressionType;
			this.exp1 = new Expression(e.exp1);
		} else {
			this.expressionType = e.expressionType;
			this.exp1 = new Expression(e.exp1);
			this.exp2 = new Expression(e.exp2);
		}
	}

	public double evaluate() throws IllegalStateException{
		if(expressionType == EXP_UNDETERMINED){
			throw new IllegalStateException("This expression is not yet determined");
		}

		switch(expressionType){
			case EXP_NUMBER:
				return number.evaluate();
			case EXP_NEGATIVE:
				return -1 * exp1.evaluate();
			case EXP_PARENTHESIS:
				return exp1.evaluate();
		    case EXP_ADD:
		    	return exp1.evaluate() + exp2.evaluate();
			case EXP_SUBTRACT:
				return exp1.evaluate() - exp2.evaluate();
			case EXP_MULTIPLY:
				return exp1.evaluate() * exp2.evaluate();
			case EXP_DIVIDE:
				if(exp2.evaluate() == 0.0){
					throw new ArithmeticException("Devision by zero");
				}else{
					return exp1.evaluate() / exp2.evaluate();
				}
			case EXP_REMAINDER:
				return exp1.evaluate() % exp2.evaluate();
			case EXP_EXPONENT:
				return Math.pow(exp1.evaluate(), exp2.evaluate());
			case EXP_GREATER_THAN:
				return exp1.evaluate() > exp2.evaluate() ? 1 : 0;
			case EXP_LESS_THAN:
				return exp1.evaluate() < exp2.evaluate() ? 1 : 0;
			case EXP_IDENTIFIER:
				if(MoveSystem.isAssigned(identifier.getName())){
					return MoveSystem.get(identifier.getName());
				}else{
					throw new IllegalStateException(identifier.getName() + " not assigned");
				}

		}

		return Double.NaN;
	}

	public MoveElement[][] getExpansions(){
		MoveElement[][] expansions = new MoveElement[12][];
		MoveElement[] exp0 = {new MoveNumber()};
		expansions[0] = exp0;
		MoveElement[] exp1 = {MoveToken.SUBTRACT_TOKEN, new Expression()};
		expansions[1] = exp1;
		MoveElement[] exp2 = {MoveToken.LEFT_PARENTHESIS_TOKEN, new Expression(), MoveToken.RIGHT_PARENTHESIS_TOKEN};
		expansions[2] = exp2;
		MoveElement[] exp3 = {new Expression(), MoveToken.ADD_TOKEN, new Expression()};
		expansions[3] = exp3;
		MoveElement[] exp4 = {new Expression(), MoveToken.SUBTRACT_TOKEN, new Expression()};
		expansions[4] = exp4;
		MoveElement[] exp5 = {new Expression(), MoveToken.MULTIPLY_TOKEN, new Expression()};
		expansions[5] = exp5;
		MoveElement[] exp6 = {new Expression(), MoveToken.DIVIDE_TOKEN, new Expression()};
		expansions[6] = exp6;
		MoveElement[] exp7 = {new Expression(), MoveToken.REMAINDER_TOKEN, new Expression()};
		expansions[7] = exp7;
		MoveElement[] exp8 = {new Expression(), MoveToken.CARROT_TOKEN, new Expression()};
		expansions[8] = exp8;
		MoveElement[] exp9 = {new Expression(), MoveToken.GREATER_THAN_TOKEN, new Expression()};
		expansions[9] = exp9;
		MoveElement[] exp10 = {new Expression(), MoveToken.LESS_THAN_TOKEN, new Expression()};
		expansions[10] = exp10;
		MoveElement[] exp11 = {new MoveIdentifier()};
		expansions[11] = exp11;
		return expansions;
	}

	public void build(MoveElement[] exp) throws IllegalStateException{
		if(exp.length == 1){ // this is a Number or identifier
			if(exp[0] instanceof MoveNumber){
				expressionType = EXP_NUMBER;
				number = (MoveNumber)exp[0];
			}else {
				expressionType = EXP_IDENTIFIER;
				identifier = (MoveIdentifier)exp[0];
			}
		} else if(exp.length == 2){ // this is a negated expression
			expressionType = EXP_NEGATIVE;
			exp1 = (Expression)exp[1];
		} else if(exp.length == 3){
			if(exp[0] instanceof MoveToken && ((MoveToken)exp[0]).matches(MoveToken.LEFT_PARENTHESIS_TOKEN) && ((MoveToken)exp[2]).matches(MoveToken.RIGHT_PARENTHESIS_TOKEN)){ // this is a composition (EXP)
				expressionType = EXP_PARENTHESIS;
				exp1 = (Expression)exp[1];
			}else if(((MoveToken)exp[1]).matches(MoveToken.ADD_TOKEN)){
				expressionType = EXP_ADD;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.SUBTRACT_TOKEN)){
				expressionType = EXP_SUBTRACT;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.MULTIPLY_TOKEN)){
				expressionType = EXP_MULTIPLY;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.DIVIDE_TOKEN)){
				expressionType = EXP_DIVIDE;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.REMAINDER_TOKEN)){
				expressionType = EXP_REMAINDER;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.CARROT_TOKEN)){
				expressionType = EXP_EXPONENT;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.LESS_THAN_TOKEN)){
				expressionType = EXP_LESS_THAN;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}else if(((MoveToken)exp[1]).matches(MoveToken.GREATER_THAN_TOKEN)){
				expressionType = EXP_GREATER_THAN;
				exp1 = (Expression)exp[0];
				exp2 = (Expression)exp[2];
			}
		}else{
			throw new IllegalStateException("Length of Expression expansion is" + exp.length);
		}
	}

	public boolean equals(Expression ex){
		if(expressionType == ex.expressionType){
			if(number != null && ex.number != null && !number.equals(ex.number)){
				return false;
			}
			if(exp1 != null && ex.exp1 != null && !exp1.equals(ex.exp1)){
				return false;
			}
			if(exp2 != null && ex.exp2 != null && !exp2.equals(ex.exp2)){
				return false;
			}
			if(identifier != null && ex.identifier != null && !identifier.equals(ex.identifier)){
				return false;
			}
			return true;
		}else{
			return false;
		}
	}

	public int compareTo(Expression e){ // this measures precedence
		if(expressionType == EXP_NUMBER || expressionType == EXP_PARENTHESIS ||
		   expressionType == EXP_NEGATIVE || expressionType == EXP_IDENTIFIER){
		   	return 1; // the passed expression is precedent
		}
		if(e.expressionType == EXP_NUMBER || e.expressionType == EXP_PARENTHESIS ||
		   e.expressionType == EXP_NEGATIVE || e.expressionType == EXP_IDENTIFIER){
		   	return 1; // the passed expression is precedent
		}

		int exponent = 0;
		int multiply = 1;
		int divide   = 1;
		int remainder = 1;
		int add       = 2;
		int subtract  = 2;
		int greater   = 3;
		int less      = 3;

		int thisPrecedence = -1;
		int ePrecedence    = -1;

		switch(expressionType){
			case EXP_ADD:
				thisPrecedence = add; break;

			case EXP_SUBTRACT:
				thisPrecedence = subtract; break;
			case EXP_MULTIPLY:
				thisPrecedence = multiply; break;
			case EXP_DIVIDE:
				thisPrecedence = divide; break;
			case EXP_REMAINDER:
				thisPrecedence = remainder; break;
			case EXP_EXPONENT:
				thisPrecedence = exponent; break;
			case EXP_GREATER_THAN:
				thisPrecedence = greater; break;
			case EXP_LESS_THAN:
				thisPrecedence = less; break;
		}

		switch(e.expressionType){
			case EXP_ADD:
				ePrecedence = add; break;
			case EXP_SUBTRACT:
				ePrecedence = subtract; break;
			case EXP_MULTIPLY:
				ePrecedence = multiply; break;
			case EXP_DIVIDE:
				ePrecedence = divide; break;
			case EXP_REMAINDER:
				ePrecedence = remainder; break;
			case EXP_EXPONENT:
				ePrecedence = exponent; break;
			case EXP_GREATER_THAN:
				ePrecedence = greater; break;
			case EXP_LESS_THAN:
				ePrecedence = less; break;
		}

		return thisPrecedence - ePrecedence;

	}

	public boolean respectsPrecedence() throws IllegalStateException{
		switch(expressionType){
			case EXP_NUMBER:
				return true;
			case EXP_IDENTIFIER:
				return true;
			case EXP_NEGATIVE:
				return exp1.respectsPrecedence();
			case EXP_PARENTHESIS:
				return exp1.respectsPrecedence();
			case EXP_ADD:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_SUBTRACT:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_MULTIPLY:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_DIVIDE:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_REMAINDER:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_EXPONENT:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_GREATER_THAN:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_LESS_THAN:
				return compareTo(exp1) > 0 && compareTo(exp2) >= 0 && exp1.respectsPrecedence() && exp2.respectsPrecedence();
			case EXP_UNDETERMINED:
				throw new IllegalStateException();
			default:
				return false;
		}
	}

	public String toString(){
		switch(expressionType){
			case EXP_NUMBER:
				return "E<"+number.toString()+">";
			case EXP_NEGATIVE:
				return "E<-" + exp1 +">";
			case EXP_PARENTHESIS:
				return "E<(" + exp1 + ")>";
		    case EXP_ADD:
		    	return "E<" + exp1 + "+" + exp2 + ">";
			case EXP_SUBTRACT:
				return "E<" + exp1 + "-" + exp2 + ">";
			case EXP_MULTIPLY:
				return "E<" + exp1 + "*" + exp2 + ">";
			case EXP_DIVIDE:
				return "E<" + exp1 + "/" + exp2 + ">";
			case EXP_REMAINDER:
				return "E<" + exp1 + "%" + exp2 + ">";
			case EXP_EXPONENT:
				return "E<" + exp1 + "^" + exp2 + ">";
			case EXP_GREATER_THAN:
				return "E<" + exp1 + ">" + exp2 + ">";
			case EXP_LESS_THAN:
				return "E<" + exp1 + "<" + exp2 + ">";
			case EXP_UNDETERMINED:
				return "EXP UND";
		    case EXP_IDENTIFIER:
		    	return "E<"+identifier+">";
		}
		return "BBBB";
	}
}