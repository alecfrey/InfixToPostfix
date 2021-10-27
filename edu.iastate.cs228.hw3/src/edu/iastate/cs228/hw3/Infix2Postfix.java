package edu.iastate.cs228.hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

// 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
public class Infix2Postfix {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		File input = new File("input.txt");
		File output = new File("output.txt");
		
		// temp store operators awaiting their right hand side operand
		Stack opStack = new Stack();
		
		Scanner s = new Scanner(input);

		while (s.hasNextLine()) {
			
			while(s.hasNext()) {
				
				if (s.hasNextInt()) {
					
					
					
					
				} else {
					
					
					
					
					
					
					
					
					
					
					
					
				/**
				 * while (!opStack.isEmpty() && prec(x) <= prec(opStack.peek())) {
				 * 		y = opStack.pop();
				 * 		append y to postfix
				 * }
				 * opStack.push(x);
				 */
					
				}
			
				s.nextLine();
			}

			
			/**
			if (error detected in infix) {
				print first detected error
			
			}
			*/
		
		
		
		}
		s.close();
	}

}
