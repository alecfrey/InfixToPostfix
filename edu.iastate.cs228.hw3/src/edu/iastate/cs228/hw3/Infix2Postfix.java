package edu.iastate.cs228.hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;

/**
* 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
* @author Alec Frey
* 
* This program converts from infix to postfix.
* If there are errors in the given input expression, they are outputted instead of the converted expression.
*
*/
public class Infix2Postfix {

	/**
	 * Stores number of parenthesis
	 * If negative, too many closed parentheses
	 * If positive, too many open parentheses
	 * If zero, equal amount of open and closed parentheses
	 */
	private static int parenthesis = 0;

	public static void main(String[] args) throws FileNotFoundException {

		File input = new File("input.txt");
		File output = new File("output.txt");
		PrintWriter outputPrinter = new PrintWriter(output);

		// temporarily stores operators
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

				// Check if negative sign attached to number & that i + 1 isn't past string length
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

						if (prec(c) == prec(stack.peek()) && prec(c) == 3) {
							break;
						}
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
			
			// Prints this if there is an empty subexpression
			} else if (emptyParenthesis) {
				printToOutput("Error: no subexpression detected ()", outputPrinter);
				
			// Prints this if there are multiple operators in a row
			} else if (tooManyOperators) {
				printToOutput("Error: too many operators (" + currentInputLine.charAt(indexOfExtraOperator) + ")", outputPrinter);
				
			// Prints this if there are multiple operands in a row
			} else if (tooManyOperands && extraOperand != "") {
				printToOutput("Error: too many operands (" + extraOperand + ")", outputPrinter);
				
			// Last check to see if all parentheses have been opened/closed
			} else if (parenthesis > 0) {
				printToOutput("Error: no closing parenthesis detected", outputPrinter);
				
			} else if (parenthesis < 0) {
				printToOutput("Error: no opening parenthesis detected", outputPrinter);
				
			// Prints expression as normal if no other conditions were met
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

	/**
	 * Prints given input to output.txt using given PrintWriter
	 * 
	 * @param input
	 * @param outputPrinter
	 */
	private static void printToOutput(String input, PrintWriter outputPrinter) {
		outputPrinter.print(input);
	}

	/**
	 * Returns precedence of given operant
	 * 
	 * Returns 1 if + or -
	 * Returns 2 if * or / or %
	 * Returns 3 if ^
	 * 
	 * @param operant
	 * @return
	 */
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
	 * Returns value of parenthesis.
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

	/**
	 * Checks if there is an empty sub expression using the given string, and start and end indexes.
	 * 
	 * Returns true or false.
	 * 
	 * @param inputLine
	 * @param start
	 * @param end
	 * @return
	 */
	private static boolean isEmptySubExpression(String inputLine, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (Character.isDigit(inputLine.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if current index is a operator.
	 * 
	 * Starts at index of current operator and checks.
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
	 * Removes space at end of current line if there is one.
	 * 
	 * Returns same string without space if there was one.
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

	/**
	 * Used to check if there are too many operands in a row.
	 * 
	 * Returns extra operand if there is too many, else null.
	 * 
	 * @param input
	 * @param index
	 * @return
	 */
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

	/**
	 * Checks if first character is an operator with a given input string.
	 * 
	 * Returns true or false.
	 * 
	 * @param input
	 * @return
	 */
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