package move;
import java.util.regex.Pattern;
import java.util.LinkedList;

public class MoveTokenizer{
	public static LinkedList<MoveToken> tokenize(String program){

		LinkedList<MoveToken> tokens = new LinkedList<MoveToken>();
		int pointer = 0;
		int length = 1;
		int bestLength = 0;
		boolean possibleNumber = true;
		boolean possibleIdentifier = true;
		MoveToken bestToken = null;

		while(pointer != program.length()){
			if(pointer+length > program.length()){
				possibleIdentifier = false;
				possibleNumber = false;
			}

			if(length == 1 && program.charAt(pointer) == ' '){
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '+'){
				tokens.add(MoveToken.ADD_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '-'){
				tokens.add(MoveToken.SUBTRACT_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '*'){
				tokens.add(MoveToken.MULTIPLY_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '/'){
				tokens.add(MoveToken.DIVIDE_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '^'){
				tokens.add(MoveToken.CARROT_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '%'){
				tokens.add(MoveToken.REMAINDER_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '<'){
				tokens.add(MoveToken.LESS_THAN_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '>'){
				tokens.add(MoveToken.GREATER_THAN_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '('){
				tokens.add(MoveToken.LEFT_PARENTHESIS_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == ')'){
				tokens.add(MoveToken.RIGHT_PARENTHESIS_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '='){
				tokens.add(MoveToken.EQUALS_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '\n'){
				tokens.add(MoveToken.NEW_LINE_TOKEN);
				pointer++;
			}else if(length == 1 && program.charAt(pointer) == '!'){ // commented line
				return tokens;
			}else{
				if(possibleIdentifier){
					possibleIdentifier = isIdentifier(program.substring(pointer, pointer+length));
					if(possibleIdentifier){
						String rep = program.substring(pointer, pointer+length);
						if(rep.equals("move")){
							bestToken = MoveToken.MOVE_TOKEN;
							bestLength = 4;
						}else if(rep.equals("print")){
							bestToken = MoveToken.PRINT_TOKEN;
							bestLength = 5;
						}else{
							bestToken = new MoveIdentifier(program.substring(pointer, pointer+length));
							bestLength = length;
						}
					}
				}
				if(possibleNumber){
  					possibleNumber = possibleNumber(program.substring(pointer, pointer+length));
					if(possibleNumber){
						try{
							bestToken = new MoveNumber(Double.parseDouble(program.substring(pointer, pointer+length)));
							bestLength = length;
						}catch(NumberFormatException ex){}
					}
				}
				if(!possibleIdentifier && !possibleNumber){
					if(bestToken != null){
						tokens.add(bestToken);
						pointer = pointer+bestLength;
						length = 1;
						bestToken = null;
						possibleNumber = true;
						possibleIdentifier = true;
					}else{
						throw new IllegalArgumentException(String.valueOf(pointer) + " " + String.valueOf(length));
					}
				}else{
					length++;
				}
			}
		}
		return tokens;
	}

	public static boolean isIdentifier(String name){
		return Pattern.matches("[A-Za-z_][A-Za-z0-9_]*", name);
	}

	public static boolean possibleNumber(String text){
		if(text.equals(".")){
			return true;
		}else if(text.contains(" ") || text.contains("\n")){
			return false;
		}else{
			try{
				Double.parseDouble(text);
				return true;
			}catch(NumberFormatException ex){
				return false;
			}
		}
	}

	public static void main(String[] args){
		String program = "a = 3\n" +
						 "b = a + 2";
        System.out.println(tokenize(program));
	}
}