package move;
public class MoveToken implements MoveElement{
	public static final int GREATER_THAN = 0;
	public static final int LESS_THAN = 1;
	public static final int ADD = 2;
	public static final int SUBTRACT = 3;
	public static final int MULTIPLY = 4;
	public static final int DIVIDE = 5;
	public static final int REMAINDER = 6;
	public static final int CARROT = 7;
	public static final int LEFT_PARENTHESIS = 8;
	public static final int RIGHT_PARENTHESIS = 9;
	public static final int EQUALS = 10;
	public static final int MOVE = 11;
	public static final int PRINT = 12;
	public static final int NEW_LINE = 13;

	public static final int IDENTIFIER = 14;
	public static final int NUMBER = 15;

	public static final MoveToken ADD_TOKEN;
	public static final MoveToken SUBTRACT_TOKEN;
	public static final MoveToken MULTIPLY_TOKEN;
	public static final MoveToken DIVIDE_TOKEN;
	public static final MoveToken CARROT_TOKEN;
	public static final MoveToken REMAINDER_TOKEN;
	public static final MoveToken GREATER_THAN_TOKEN;
	public static final MoveToken LESS_THAN_TOKEN;
	public static final MoveToken LEFT_PARENTHESIS_TOKEN;
	public static final MoveToken RIGHT_PARENTHESIS_TOKEN;
	public static final MoveToken EQUALS_TOKEN;
	public static final MoveToken MOVE_TOKEN;
	public static final MoveToken PRINT_TOKEN;
	public static final MoveToken NEW_LINE_TOKEN;

	static{
		ADD_TOKEN =               new MoveToken(MoveToken.ADD);
		SUBTRACT_TOKEN =          new MoveToken(MoveToken.SUBTRACT);
		MULTIPLY_TOKEN =          new MoveToken(MoveToken.MULTIPLY);
		DIVIDE_TOKEN =            new MoveToken(MoveToken.DIVIDE);
		CARROT_TOKEN =            new MoveToken(MoveToken.CARROT);
		REMAINDER_TOKEN =         new MoveToken(MoveToken.REMAINDER);
		GREATER_THAN_TOKEN =      new MoveToken(MoveToken.GREATER_THAN);
		LESS_THAN_TOKEN =         new MoveToken(MoveToken.LESS_THAN);
		LEFT_PARENTHESIS_TOKEN =  new MoveToken(MoveToken.LEFT_PARENTHESIS);
		RIGHT_PARENTHESIS_TOKEN = new MoveToken(MoveToken.RIGHT_PARENTHESIS);
		EQUALS_TOKEN =            new MoveToken(MoveToken.EQUALS);
		MOVE_TOKEN =              new MoveToken(MoveToken.MOVE);
		PRINT_TOKEN =             new MoveToken(MoveToken.PRINT);
		NEW_LINE_TOKEN =          new MoveToken(MoveToken.NEW_LINE);
	}

	private int type;

	public MoveToken(int t){
		type = t;
	}

	public MoveToken(MoveToken t){
		this.type = t.type;
	}

	public boolean matches(MoveToken t){
		return this.type == t.type;
	}

	public int getType(){return type;}
	public String toString(){
		switch(type){
			case ADD: return "+";
			case SUBTRACT:          return "-";
			case MULTIPLY:          return "*";
			case DIVIDE:            return "/";
			case CARROT:            return "^";
			case REMAINDER:         return "%";
			case GREATER_THAN:      return ">";
			case LESS_THAN:         return "<";
			case LEFT_PARENTHESIS:  return "(";
			case RIGHT_PARENTHESIS: return ")";
			case EQUALS:            return "=";
			case MOVE:              return "MOVE";
			case PRINT:             return "PRINT";
			case NEW_LINE:          return "NEW_LINE";
			default:                return "UNKNOWN";
		}
	}

	public boolean isMorePresidentThan(MoveToken token){
		int exponent = 0;
		int multiply = 1;
		int divide = 1;
		int remainder = 1;
		int add = 2;
		int subtract = 2;
		int greater = 3;
		int less = 3;

		int i1 = 0;
		int i2 = 0;
		switch(type){
			case GREATER_THAN:
				i1 = greater;
			case LESS_THAN:
				i1 = less;
			case ADD:
				i1 = add;
			case SUBTRACT:
				i1 = subtract;
			case MULTIPLY:
				i1 = multiply;
			case DIVIDE:
				i1 = divide;
			case REMAINDER:
				i1 = remainder;
			case CARROT:
				i1 = exponent;
		}

		switch(token.type){
			case GREATER_THAN:
				i2 = greater;
			case LESS_THAN:
				i2 = less;
			case ADD:
				i2 = add;
			case SUBTRACT:
				i2 = subtract;
			case MULTIPLY:
				i2 = multiply;
			case DIVIDE:
				i2 = divide;
			case REMAINDER:
				i2 = remainder;
			case CARROT:
				i2 = exponent;
		}

		return i1 < i2;
	}
}