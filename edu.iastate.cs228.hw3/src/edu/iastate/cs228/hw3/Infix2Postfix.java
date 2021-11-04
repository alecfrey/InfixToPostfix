package edu.iastate.cs228.hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;

// 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
public class Infix2Postfix {
	
	private static int parenthesis = 0;

	public static void main(String[] args) throws FileNotFoundException {
		
		File input = new File("input.txt");
		File output = new File("output.txt");
		PrintWriter outputPrinter = new PrintWriter(output);

		// temp store operators awaiting their right hand side operand
		Stack<Character> stack = new Stack<>();

		Scanner s = new Scanner(input);

		// Convert each input line
		while (s.hasNextLine()) {
			String postfixString = "";
			String currentInputLine = s.nextLine();

			// Loop through sorting numbers and operants
			for (int i = 0; i < currentInputLine.length(); i++) {
				char c = currentInputLine.charAt(i);
				
				//Check if negative sign attached to number & that i + 1 isn't past string length
				if ((c == '-') && (i + 1 < currentInputLine.length()) && (Character.isDigit(currentInputLine.charAt(i + 1)))) {
					String num = "" + c;

					// Check if next character is a number also (Example: 125)
					while (i + 1 < currentInputLine.length() && Character.isDigit(currentInputLine.charAt(i + 1))) {
						i++;
						num += currentInputLine.charAt(i);
					}
					postfixString += num + " ";
					
				// Check if positive number
				} else if (Character.isDigit(c)) {
						String num = "" + c;

						// Check if next character is a number also (Example: 125)
						while (i + 1 < currentInputLine.length() && Character.isDigit(currentInputLine.charAt(i + 1))) {
							i++;
							num += currentInputLine.charAt(i);
						}
						postfixString += num + " ";

				// Check if character is open parenthesis
				} else if (c == '(') {
					if (parenthesis < 0) {
						postfixString = "Error: no closing parenthesis detected";
						break;
					}
					
					stack.push(c);
					numParentheses(1);

				// Check if character is close parenthesis=
				} else if (c == ')') {
					if (parenthesis > 0) {
						numParentheses(-1);

						while (!stack.isEmpty() && stack.peek() != '(') {
							postfixString += stack.pop() + " ";
							//System.out.println(postfixString);
						}
						stack.pop();
						
					} else {
						postfixString = "Error: no opening parenthesis detected";
						break;
					}

				// Character must be operator
				} else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^'){
					while ((!stack.isEmpty()) && (prec(c) <= prec(stack.peek()))) {
						postfixString += stack.pop() + " ";
						//System.out.println(postfixString);
					}
					stack.push(c);
				}
			}

			// Pop operators
			while (!stack.isEmpty()) {
				postfixString += stack.pop() + " ";
				//System.out.println(postfixString);
			}
			
			System.out.println();
			if (postfixString.charAt(postfixString.length() - 1) == ' ') {
				int end = postfixString.length() - 1;
				postfixString = postfixString.substring(0, end);
			}

			// Check if all parentheses have been closed 
			if (parenthesis > 0) {
				printToOutput("Error: no closing parenthesis detected", outputPrinter);
			} else if (parenthesis < 0) {
				printToOutput("Error: no opening parenthesis detected", outputPrinter);
			} else {
				printToOutput(postfixString, outputPrinter);
			}
			
			parenthesis = 0;

			if (s.hasNextLine()) {
				printToOutput("\n", outputPrinter);
			}
		}

		s.close();
		stack.forEach(System.out::println);
		outputPrinter.close();
	}

	private static void printToOutput(String input, PrintWriter outputPrinter) {
		outputPrinter.print(input);
	}

	private static int prec(char operant) {
		if (operant == '+' || operant == '-') {
			return 1;
		} else if (operant == '*' || operant == '/' || operant == '%') {
			return 2;
		} else if (operant == '^') {
			return 3;
		}
		return -1;
	}
	
	/**
	 * Keeps track of number of open and closed parentheses.
	 * @param type
	 * @return
	 */
	private static int numParentheses(int type) {
		if (type == 1) {
			parenthesis++;
		} else {
			parenthesis--;
		}
		return parenthesis;
	}

}