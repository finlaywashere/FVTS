package ca.team2706.fvts.test.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Test;

import ca.team2706.fvts.core.Log;

public class LogTest {
	@Test
	public void testLog() throws IOException {
		PipedInputStream in = new PipedInputStream();
		PrintStream out = new PrintStream(new PipedOutputStream(in));
		PrintStream originalOut = System.out;
		PrintStream originalErr = System.err;
		System.setOut(out);
		System.setErr(out);
		
		Scanner scan = new Scanner(in);
		
		String message = "test1234";
		Log.d(message, false);
		assertEquals("Debug: "+message,scan.nextLine());
		Log.e(message, false);
		assertEquals("Error: "+message,scan.nextLine());
		Log.v(message, false);
		assertEquals("Verbose: "+message,scan.nextLine());
		Log.i(message, false);
		assertEquals("Info: "+message, scan.nextLine());
		
		scan.close();
		
		System.setOut(originalOut);
		System.setErr(originalErr);
	}
}
