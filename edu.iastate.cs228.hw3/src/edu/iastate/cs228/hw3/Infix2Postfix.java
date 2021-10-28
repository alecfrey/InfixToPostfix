package edu.iastate.cs228.hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;

// 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
public class Infix2Postfix {
	
	public static void main(String[] args) throws FileNotFoundException {
		File input = new File("input.txt");
		File output = new File("output.txt");
		PrintWriter outputPrinter = new PrintWriter(output);
		
		// temp store operators awaiting their right hand side operand
		Stack<Character> stack = new Stack<>();
		
		Scanner s = new Scanner(input);
		String result = "";

		// Convert each input line
		while (s.hasNextLine()) {
			String toPostfix = s.nextLine();
			
			// Loop through sorting numbers and operants
			for (int i = 0; i < toPostfix.length(); i++) {
				char c = toPostfix.charAt(i);
				
				// Check if current char is number
				if (Character.isDigit(c)) {
					String num = "" + c;
					
					// Check if next char is number also (Example: 125)
					while(i + 1 < toPostfix.length() && Character.isDigit(toPostfix.charAt(i+1))) {
						i++;
						num += toPostfix.charAt(i);
					}
										
					printToOutput(num, outputPrinter);
					result += num;
					
				} else {
					//If the precedence of the scanned operator is greater than the precedence 
					//of the operator in the stack(or the stack is empty or the stack contains a ‘(‘ ), push it.
					
					if (c == '(') {
						stack.push(c);
						
					} else if (c == ')') {
						while (!stack.isEmpty() && stack.peek() != '(') {
							result += stack.pop();
							stack.pop();
						}
						
					} else {
						while (!stack.isEmpty() && prec(c) <= prec(stack.peek())) {
							result += stack.pop();
						}
						stack.push(c);
					}
					
					//if (stack.isEmpty() || precedence > operantPrecedence(stack.peek())) {
					//	stack.push(c);
					//} 
	
			}
				
				/**
				 * while (!opStack.isEmpty() && prec(x) <= prec(opStack.peek())) {
				 * 		y = opStack.pop();
				 * 		append y to postfix
				 * }
				 * opStack.push(x);
				 */
	
			
			}
			/**
			if (error detected in infix) {
				print first detected error
			
			}
			*/
		}
		
		s.close();
		stack.forEach(System.out::println);
		outputPrinter.close();
	}
	
	
	private static void printToOutput(String input, PrintWriter outputPrinter) {
		outputPrinter.print(input + " ");
	
	}
	
	private static int prec(char operant) {
		if (operant == '+' || operant == '-' ) {
			return 1;
		} else if (operant == '*' || operant == '/' ) {
			return 2;
		} else if (operant == '^') {
			return 3;
		}
		return -1;
	}

}
