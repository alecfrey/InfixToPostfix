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

			// Empty input line, outputs empty line
			if (currentInputLine == "") {
				if (s.hasNextLine()) {
					printToOutput("\n", outputPrinter);
				}
				continue;
			}

			boolean emptyParenthesis = false;

			boolean tooManyOperators = false;
			int indexOfExtraOperator = 0;

			boolean tooManyOperands = false;
			String extraOperand = null;

			// Loop through sorting numbers and operants
			for (int i = 0; i < currentInputLine.length(); i++) {
				char c = currentInputLine.charAt(i);

				// Check if negative sign attached to number & that i + 1 isn't past string
				// length
				if ((c == '-') && (i + 1 < currentInputLine.length()) && (Character.isDigit(currentInputLine.charAt(i + 1)))) {
					String num = "" + c;

					// Check if next character is a number also (Example: 125)
					while (i + 1 < currentInputLine.length() && Character.isDigit(currentInputLine.charAt(i + 1))) {
						i++;
						num += currentInputLine.charAt(i);
					}
					postfixString += num + " ";

					extraOperand = tooManyOperands(currentInputLine, i);
					if (extraOperand != null) {
						tooManyOperands = true;
						break;
					}

					// Check if positive number
				} else if (Character.isDigit(c)) {
					String num = "" + c;

					// Check if next character is a number also (Example: 125)
					while (i + 1 < currentInputLine.length() && Character.isDigit(currentInputLine.charAt(i + 1))) {
						i++;
						num += currentInputLine.charAt(i);
					}
					postfixString += num + " ";

					extraOperand = tooManyOperands(currentInputLine, i);
					if (extraOperand != null) {
						tooManyOperands = true;
						break;
					}

					// Check if character is open parenthesis
				} else if (c == '(') {
					if (parenthesis < 0) {
						parenthesis = 1;
						break;
					}

					stack.push(c);
					numParentheses(1);

					// Check if empty subexpression
					for (int j = i; j < currentInputLine.length(); j++) {
						if (currentInputLine.charAt(j) == ')') {
							emptyParenthesis = isEmptySubExpression(currentInputLine, i, j);
							break;
						}
					}

					// if (emptyParenthesis) {
					// break;
					// }

					// Check if character is close parenthesis
				} else if (c == ')') {
					if (parenthesis > 0) {
						numParentheses(-1);

						while (!stack.isEmpty() && stack.peek() != '(') {
							postfixString += stack.pop() + " ";
						}
						stack.pop();

					} else {
						parenthesis = -1;
						break;
					}

					// Character must be operator
				} else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^') {
					while ((!stack.isEmpty()) && (prec(c) <= prec(stack.peek()))) {
						postfixString += stack.pop() + " ";
					}
					
					for (int j = i + 1; j < currentInputLine.length(); j++) {
						if (Character.isDigit(currentInputLine.charAt(j))) {
							break;
						} else if (isOperator(currentInputLine, j)) {

							// Makes sure extra operator isn't a minus related to a negative number
							if (j + 1 < currentInputLine.length() && Character.isDigit(currentInputLine.charAt(j + 1))) {
								break;
							}
							tooManyOperators = true;
							indexOfExtraOperator = j;
							break;
							 
						} 
					}
				
					// MAY BE BAD
					currentInputLine = removeLastSpace(currentInputLine);
					if (i == currentInputLine.length() - 1) {
						tooManyOperators = true;
						indexOfExtraOperator = i;
						break;
					}
					
					stack.push(c);
				}
			}

			// Pop operators
			while (!stack.isEmpty()) {
				postfixString += stack.pop() + " ";
			}

			postfixString = removeLastSpace(postfixString);

			// Checks if the current line starts with an operator
			if (isFirstCharacterAnOperator(currentInputLine)) {
				for (int i = 0; i < currentInputLine.length(); i++) {
					if (isOperator(currentInputLine, i)) {
						printToOutput("Error: too many operators (" + currentInputLine.charAt(i) + ")", outputPrinter);
						break;
					}
				}
			
			// Last check to see if there is an empty subexpression and if all parentheses have been closed
			} else if (emptyParenthesis) {
				printToOutput("Error: no subexpression detected ()", outputPrinter);
				
			} else if (tooManyOperators) {
				printToOutput("Error: too many operators (" + currentInputLine.charAt(indexOfExtraOperator) + ")", outputPrinter);
				
			} else if (tooManyOperands && extraOperand != "") {
				printToOutput("Error: too many operands (" + extraOperand + ")", outputPrinter);
				
			} else if (parenthesis > 0) {
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
	 * 
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

	private static boolean isEmptySubExpression(String inputLine, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (Character.isDigit(inputLine.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if current index is a operator
	 * 
	 * Starts at index of current operator and checks
	 * 
	 * @param input
	 * @param start
	 * @return
	 */
	private static boolean isOperator(String input, int index) {
		if (input.charAt(index) == '+' || input.charAt(index) == '-' || input.charAt(index) == '*'
				|| input.charAt(index) == '/' || input.charAt(index) == '%' || input.charAt(index) == '^') {
			return true;
		}
		return false;
	}

	/**
	 * If there is a space at end of current line it is deleted
	 * 
	 * Returns same string without space if there was one
	 * 
	 * @param s
	 * @return input line with no end space
	 */
	private static String removeLastSpace(String s) {
		if (s.length() > 0 && s.charAt(s.length() - 1) == ' ') {
			int end = s.length() - 1;
			s = s.substring(0, end);
		}
		return s;
	}

	private static String tooManyOperands(String input, int index) {
		String operand = "";
		for (int i = index + 1; i < input.length(); i++) {
			if (Character.isDigit(input.charAt(i))) {
				operand += input.charAt(i);
				while (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
					i++;
					operand += input.charAt(i);
				}
				break;

			} else if (isOperator(input, i) || i == input.length() - 1) {

				// Check if operator detected is unary minus
				if (isUnaryMinus(input, i)) {
					operand += "-";
					while (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
						i++;
						operand += input.charAt(i);
					}
					return operand;
				}
				return operand = null;
			}
		}
		return operand;
	}

	/**
	 * Check if detected "-" is a subtraction sign or a unary minus ("-15)
	 * 
	 * Returns true or false
	 * 
	 * @param input
	 * @param minusIndex
	 * @return
	 */
	private static boolean isUnaryMinus(String input, int minusIndex) {
		if (minusIndex + 1 < input.length()) {
			if (Character.isDigit(input.charAt(minusIndex + 1))) {
				return true;
			}
		}
		return false;
	}

	private static boolean isFirstCharacterAnOperator(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (isOperator(input, i) && !isUnaryMinus(input, i)) {
				return true;
			} else if (Character.isDigit(input.charAt(i))) {
				return false;
			}
		}
		return false;
	}
}