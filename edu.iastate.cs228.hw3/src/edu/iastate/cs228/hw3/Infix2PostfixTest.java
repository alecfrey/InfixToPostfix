package edu.iastate.cs228.hw3;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


public class Infix2PostfixTest {
	static String[] inputLines;

	// saves the input in the input file so that it can be replaced after tests have
	// been completed
	@BeforeAll
	public static void recordInput()
	{
		try
		{
			List<String> l = Files.readAllLines(Paths.get("input.txt"), StandardCharsets.UTF_8);
			inputLines = l.toArray(new String[l.size()]);
		} catch (IOException e)
		{
			fail("input.txt does not exist in the default directory");
		}
	}

	// puts the input back into the input file
	@AfterAll
	public static void putInputBack()
	{
		File inputFile = new File("input.txt");

		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(inputFile);
		} catch (Exception e)
		{
			fail("input.txt does not exist in the default directory");
		}

		for (int i = 0; i < inputLines.length; i++)
		{
			writer.print(inputLines[i]);

			if (i < inputLines.length - 1)
			{
				writer.println();
			}
		}
		writer.close();
	}

	void genericTest(String input, String expectedOutput) throws FileNotFoundException
	{
		// access the input file and output files
		File inputFile = new File("input.txt");
		File outputFile = new File("output.txt");

		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(inputFile);
		} catch (Exception e)
		{
			fail("input.txt does not exist in the default directory");
		}

		Scanner scan = null;
		try
		{
			scan = new Scanner(outputFile);
		} catch (Exception e)
		{
			fail("output.txt does not exist in the default directory");
		}

		// write the input to the input file
		writer.print(input);
		writer.close();

		// run the program
		Infix2Postfix.main(null);

		// read from the output file
		StringBuilder output = new StringBuilder();
		while (scan.hasNextLine())
		{
			if (!output.isEmpty())
				output.append("\n");

			String line = scan.nextLine();

			if (!line.isEmpty())
				output.append(line);
		}

		// check the output and make sure it matches the expected output
		assertEquals(expectedOutput, output.toString());
	}

	@Test
	@Order(1)
	void basicTest() throws FileNotFoundException
	{
		// all these should be sorted properly
		// if you think it is wrong, please manually check it and then notify me if it
		// is wrong

		// example input from the pdf
		genericTest("12 + ( ( 23 * ( 4 ^ ( 3 - ( 7 / ( 11 ^ 2 ) ) ) ) ) % 25 )", "12 23 4 3 7 11 2 ^ / - ^ * 25 % +");

		genericTest("3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3", "3 4 2 * 1 5 - 2 3 ^ ^ / +");

		genericTest("( ( ( 5 + 7 ) ) )", "5 7 +");

		genericTest("-69", "-69");

		genericTest("-4 - -20", "-4 -20 -");
	}

	@Test
	@Order(2)
	void tooManyOperators() throws FileNotFoundException
	{
		// example input from the pdf
		genericTest("7 ^ ^ 3 ^ ( 4 * ( -15 - -2 ) ) / ( ( 29 + 7 ) ^ 2 )", "Error: too many operators (^)");

		genericTest("^ 6", "Error: too many operators (^)");

		genericTest("6 ^", "Error: too many operators (^)");

		genericTest("%", "Error: too many operators (%)");

		/*
		 * From the pdf: "The detected error may or may not be the first error in the
		 * expression. For example, in the expression 5 ( + 5 ) the first error detected
		 * may be that + 5 has too many operators (+), while the second error detected
		 * may be that there are too many operands (5) for the first token in the
		 * expression."
		 * 
		 */
		//genericTest("5 ( + 5 )", "Error: too many operators (+)");
	}

	@Test
	@Order(3)
	void tooManyOperands() throws FileNotFoundException
	{
		genericTest("2 * 3 45 6", "Error: too many operands (45)");

		// testing too many operands when surrounded with parenthesis
		genericTest("( 6 + 3 ) ( -5 )", "Error: too many operands (-5)");

		genericTest("1 2", "Error: too many operands (2)");
	}

	@Test
	@Order(4)
	void openParenthesis() throws FileNotFoundException
	{
		// example input from pdf
		//genericTest("-4 - ) ) + 15", "Error: no opening parenthesis detected");

		genericTest("( ( 5 + 7 ) ) )", "Error: no opening parenthesis detected");

		//genericTest("-4 - ) + 15 *", "Error: no opening parenthesis detected");
	}

	@Test
	@Order(5)
	void closeParenthesis() throws FileNotFoundException
	{
		genericTest("( 5 + 3 ) (", "Error: no closing parenthesis detected");

		//genericTest("( 5 + 3 ) ( 4 7", "Error: no closing parenthesis detected");

		genericTest("( ( ( 5 + 7 ) )", "Error: no closing parenthesis detected");
	}

	@Test
	@Order(6)
	void noSubexpression() throws FileNotFoundException
	{
		// example from pdf
		genericTest("( ) 5", "Error: no subexpression detected ()");

		genericTest("5 + ( ) 8", "Error: no subexpression detected ()");

		genericTest("( ) 5 / * ^", "Error: no subexpression detected ()");
	}
}