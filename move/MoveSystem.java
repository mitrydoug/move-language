package move;
import java.util.*;
import java.io.*;
import javax.swing.*;

public class MoveSystem{

	private static final int APPLET = 0;
	private static final int APPLICATION = 1;

	private static int executionType;

	private static Hashtable<String, Double> variables;
	private static int lineNumber;

	private static JTextArea appletOutput;

	public static void assign(double v, String identifier){
		variables.put(identifier, v);
	}

	public static double get(String identifier){
		return variables.get(identifier);
	}

	public static int getLine(){return lineNumber;}

	public static void setLine(int num){lineNumber = num;}

	public static boolean isAssigned(String name){
		return variables.containsKey(name);
	}

	public static boolean enabled = false;
	public static void beginInterpreter(ArrayList<Statement> statements){
		enabled = true;
        while(enabled && lineNumber>=0 && lineNumber<statements.size()){
            try{
                statements.get(lineNumber).execute();
            }catch(IllegalStateException e){
                MoveSystem.println("Error: " + e.getMessage() + " (in statement: " + statements.get(lineNumber) + ")");
                exit();
                return;
            }catch(ArithmeticException i){
                MoveSystem.println("Devision by zero in statement: " + statements.get(lineNumber));
                exit();
                return;
            }catch(IllegalArgumentException a){
                MoveSystem.println("argument for MOVE must be an integer: " + statements.get(lineNumber));
                exit();
                return;
            }
        }
        enabled = false;
	}

	public static void main(String[] args) throws IOException{
		executionType = APPLICATION;
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line = "";
		ArrayList<Statement> statements = new ArrayList<Statement>();
		variables = new Hashtable<String, Double>();
		lineNumber = 0;
		while((line = br.readLine()) != null){
			try{
				line = line.trim();
				if(line.length() != 0 && line.charAt(0) != '!'){ //commented or empty
					statements.add(MoveParser.parse(MoveTokenizer.tokenize(line)));
				}
			}catch(IllegalStateException e){
				MoveSystem.println("Could not parse " + line);
				exit();
				return;
			}
		}
		beginInterpreter(statements);
		exit();
	}

	public static void appletMain(String program, JTextArea out){
		executionType = APPLET;
		appletOutput = out;
	    Scanner scan = new Scanner(program);
	    String line;
	    ArrayList<Statement> statements = new ArrayList<Statement>();
	    variables = new Hashtable<String, Double>();
		lineNumber = 0;
        while(scan.hasNextLine()){
            line = scan.nextLine();
            try{
                line = line.trim();
                if(line.length() != 0 && line.charAt(0) != '!'){ //commented or empty
                    statements.add(MoveParser.parse(MoveTokenizer.tokenize(line)));
                }
            }catch(IllegalStateException e){
                MoveSystem.println("Could not parse " + line);
                exit();
                return;
            }
        }
        beginInterpreter(statements);
        exit();
	}

	public static void println(String output){
		if(executionType == APPLICATION){
			System.out.println(output);
		} else if(executionType == APPLET){
			appletOutput.setText(appletOutput.getText() + output + "\n");
		}
	}

	public static void stop(){
		enabled = false;
	}

	public static boolean isEnabled(){ return enabled;}

	public static void exit(){
		MoveSystem.println("Done");
	}
}
