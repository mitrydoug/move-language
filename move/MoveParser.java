package move;
import java.util.*;

public class MoveParser{

	private static ArrayList<ListSet> stateMap;

	public static boolean debug = false;

	static{
		stateMap = new ArrayList<ListSet>();
	}

	public static class ListSet extends ArrayList<ParserState>{
		public boolean add(ParserState e){
			for(int i=0; i<size(); i++){
				if(this.get(i).equals(e)){
					return false;
				}
			}
			return super.add(e);
		}
	}

	public static class ParserState{
		private MoveStructure base; // what are we working on?
		private MoveElement[] exp;  // the expansion for the base structure
		private int index;          // to where in the structure have we confirmed? (before element at that address)
		private int from;           // where are we starting from in the program
		private int id;

		private String description;

		private static int sid = 0;

		public ParserState(MoveStructure b, MoveElement[] ex, int f, String d){
			base = b;
			exp = ex;
			index = 0;
			from = f;
			description = d;
			id = sid++;
		}

		public ParserState(ParserState s, String d){ //copy contructor. I NEED DEEP COPY!!
			if(s.base == null){
				this.base = null;
			}else if(s.base instanceof Expression){
				this.base = new Expression((Expression)s.base);
			} else if(s.base instanceof Statement){
				this.base = new Statement((Statement)s.base);
			}

			this.exp = new MoveElement[s.exp.length];
			for(int i=0; i<this.exp.length; i++){
				MoveElement el = s.exp[i];
				if(el instanceof Expression){
					this.exp[i] = new Expression((Expression)el);
				}else if(el instanceof MoveNumber){
					this.exp[i] = new MoveNumber((MoveNumber)el);
				}else if(el instanceof MoveIdentifier){
					this.exp[i] = new MoveIdentifier((MoveIdentifier)el);
				}else if(el instanceof MoveToken){
					this.exp[i] = new MoveToken((MoveToken)el);
				}else if(el instanceof Statement){
					this.exp[i] = new Statement((Statement)el);
				}
			}
			this.index = s.index;
			this.from = s.from;
			description = d;
			id = sid++;
		}

		public boolean hasNext(){
			return index != exp.length;
		}

		public MoveElement getNext(){
			return exp[index];
		}

		public void step(){
			index++;
		}

		public int getFrom(){
			return from;
		}

		public int getIndex(){return index;}

		public MoveStructure getBase(){
			return base;
		}

		public MoveElement[] getExpansion(){
			return exp;
		}

		public String toString(){
			return id + " (" + base + ", " + Arrays.toString(exp) + ", " + index + ", " + from + ") " + description;
		}

		public int getId(){return id;}

		public String getDes(){return description;}

		public void setDes(String d){description = d;}

		public boolean equals(ParserState p){
			if(from != p.from || index != p.index){
				return false;
			}else if(base == null && p.base == null){
				return((Statement)exp[0]).equals((Statement)p.exp[0]);
			}else if(base instanceof Expression && p.base instanceof Expression){
				if(exp.length == p.exp.length){
					for(int i=0; i<exp.length; i++){
						if(exp[i] instanceof Expression && p.exp[i] instanceof Expression){
							if(!((Expression)exp[i]).equals((Expression)p.exp[i])) return false;
						}else if(exp[i] instanceof MoveToken && p.exp[i] instanceof MoveToken){
							if(!((MoveToken)exp[i]).matches((MoveToken)p.exp[i])) return false;
						}else{
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			}else if(base instanceof Statement && p.base instanceof Statement){
				for(int i=0; i<exp.length; i++){
					if(exp[i] instanceof Expression && p.exp[i] instanceof Expression){
						if(!((Expression)exp[i]).equals((Expression)p.exp[i])) return false;
					}else if(exp[i] instanceof MoveToken && p.exp[i] instanceof MoveToken){
						if(!((MoveToken)exp[i]).matches((MoveToken)p.exp[i])) return false;
					}else{
						return false;
					}
				}
				return true;
			}else{
				return false;
			}
		}
	}

	public static void displayMap(int level){
		System.out.println("level: " + level);
		for(int j=0; j<stateMap.get(level).size(); j++){
			if(j!=0)System.out.print("   ");
			System.out.println(stateMap.get(level).get(j));
		}
		System.out.println();
	}

	public static Statement parse(LinkedList<MoveToken> tokens) throws IllegalStateException{
		// add the first state "I want a statment (no base, nothing confirmed, i need a statement!, from 0)"
		stateMap = new ArrayList<ListSet>();
		MoveStructure initialBase = null;
		MoveElement[] initialExpansion= {new Statement()};
		stateMap.add(new ListSet());
		stateMap.get(0).add(new ParserState(initialBase, initialExpansion, 0, "Very First State")); // from 0
		ListIterator<MoveToken> iter = tokens.listIterator();
		int tokenIndex = 0;
		while(iter.hasNext()){
			//add the next list for new ParserStates
			stateMap.add(new ListSet()); // the tokenIndex + 1 state
			MoveToken currentToken = iter.next();
			//for each of the ParserStates at the tokenIndex level, if the following element in the expansion is a token
			//and it matches the next token in the token list, add it to the next level (with Numbers and Identifiers set)
			for(int stateIndex = 0; stateIndex < stateMap.get(tokenIndex).size(); stateIndex++){
				ParserState s = stateMap.get(tokenIndex).get(stateIndex);
				// if the next element in the parserstate expansion matches the currentToken, clone the ParserState,
				//move it to the next level, and step it's index forward
				if(s.hasNext() && s.getNext() instanceof MoveToken){
					if(((MoveToken)s.getNext()).matches(currentToken)){
						ParserState newState = new ParserState(s, "Passing Token " + s.getId());
						if(newState.getNext() instanceof MoveNumber){
							((MoveNumber)newState.getNext()).setValue(((MoveNumber)currentToken).evaluate());
						}
						if(newState.getNext() instanceof MoveIdentifier){
							((MoveIdentifier)newState.getNext()).setValue(((MoveIdentifier)currentToken).toString());
						}
						newState.step();
						stateMap.get(tokenIndex + 1).add(newState);
					}
				}
				// if the next element in the parserstate is a structure, expand it and add it to the current state list level
				else if(s.hasNext() && s.getNext() instanceof MoveStructure){
					MoveElement[][] expansions = ((MoveStructure)s.getNext()).getExpansions();
					for(int eIndex=0; eIndex<expansions.length; eIndex++){
						MoveElement[] expansion = expansions[eIndex];
						MoveStructure base = null;
						if((MoveStructure)s.getNext() instanceof Expression){
							base = new Expression((Expression)(MoveStructure)s.getNext());
						}else if((MoveStructure)s.getNext() instanceof Statement){
							base = new Statement((Statement)(MoveStructure)s.getNext());
						}
						MoveElement[] newExpansion = expansion;
						ParserState newState = new ParserState(base, expansion, tokenIndex, "Expanded from " + s.getId()); //from tokenIndex
						stateMap.get(tokenIndex).add(newState);
					}
				}
				//if this state is at the end of it's expansion, we need to reduce a previous state and pull it down to new level
				else if(!s.hasNext()){
					ArrayList<ParserState> reductionList = stateMap.get(s.getFrom());
					if(s.getBase() instanceof Expression){
						for(int rStateIndex=0; rStateIndex<reductionList.size(); rStateIndex++){
							ParserState rState = reductionList.get(rStateIndex);
							if(rState.hasNext() && rState.getNext() instanceof Expression){
								ParserState newState = new ParserState(rState, "Reducing From " + s.getId());
								((MoveStructure)newState.getNext()).build(s.getExpansion());
								newState.step();
								stateMap.get(tokenIndex).add(newState);
							}
						}
					}
				}else {
					System.out.println("AHH, we've fallen through the cases of ParserStates in parse()");
				}
			}
			if(debug) displayMap(tokenIndex);
			tokenIndex ++;
		}
		//do a final reduction of the states here
		for(int stateIndex = 0; stateIndex < stateMap.get(tokenIndex).size(); stateIndex++){
			ParserState s = stateMap.get(tokenIndex).get(stateIndex);
			if(!s.hasNext()){
				ArrayList<ParserState> reductionList = stateMap.get(s.getFrom());
				if(s.getBase() instanceof Expression){
					for(int rStateIndex=0; rStateIndex<reductionList.size(); rStateIndex++){
						ParserState rState = reductionList.get(rStateIndex);
						if(rState.hasNext() && rState.getNext() instanceof Expression){
							ParserState newState = new ParserState(rState, "Reducing From 0" + s.getId());
							((MoveStructure)newState.getNext()).build(s.getExpansion());
							newState.step();
							stateMap.get(tokenIndex).add(newState);
						}
					}
				} else if(s.getBase() instanceof Statement){
					for(int rStateIndex=0; rStateIndex<reductionList.size(); rStateIndex++){
						ParserState rState = reductionList.get(rStateIndex);
						if(rState.hasNext() && rState.getNext() instanceof Statement){
							ParserState newState = new ParserState(rState, "Final State");
							((Statement)newState.getNext()).build(s.getExpansion());
							if(((Statement)newState.getNext()).getExpression().respectsPrecedence()){
								return (Statement)newState.getNext();
							}
						}
					}
				}
			}
		}
		if(debug) displayMap(tokenIndex);
		throw new IllegalStateException("This statement fell through without parsing");
	}

	public static void main(String[] args){
		try{
			String program = "move a + b";
	        System.out.println(MoveTokenizer.tokenize(program));
	        System.out.println(parse(MoveTokenizer.tokenize(program)));
		}catch(IllegalStateException e){
			System.out.println(e);
		}
	}
}
