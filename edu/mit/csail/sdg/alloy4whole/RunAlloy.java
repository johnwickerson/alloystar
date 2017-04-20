/*
MIT License

Copyright (c) 2016 by John Wickerson, Tyler Sorensen

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package edu.mit.csail.sdg.alloy4whole;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

class Globals {
  static Date lastTime;
  static long translationTime;
}

class MyReporter extends A4Reporter {

  private static String getTimestamp() {
    return (new SimpleDateFormat("HH:mm:ss").format(new Date()));
  }

  @Override public void translate(String solver, int bitwidth,
				  int maxseq, int skolemDepth,
				  int symmetry)
  {
    System.out.printf("Solver=%s Bitwidth=%d MaxSeq=%d%s Symmetry=%s\n",
		      solver, bitwidth, maxseq,
		      skolemDepth == 0 ? "" :
		      String.format(" SkolemDepth=%d",skolemDepth),
		      symmetry > 0 ? String.format("%d", symmetry) :
		      "OFF"); 
    Globals.lastTime = new Date();
  }
  
  @Override public void solve(int primaryVars, int totalVars, int clauses) {
    Date start = Globals.lastTime;
    Date end = new Date();
    long elapsed = end.getTime() - start.getTime();
    System.out.printf("%s: Translation took %d milliseconds.\n",
		      getTimestamp(), elapsed);
    System.out.printf("%d vars. %d primary vars. %d clauses.\n",
		      totalVars, primaryVars, clauses);
    Globals.lastTime = end;
    Globals.translationTime = elapsed;
  }
}

public final class RunAlloy {

  private static String getTimestamp() {
    return (new SimpleDateFormat("HH:mm:ss").format(new Date()));
  }

  public static void
    runalloy(String als_filename,
	     String xml_dir,
	     A4Options.SatSolver solver,
	     boolean higherOrderSolver,
	     int cmd_index,
	     boolean iter_flag)
    throws Err
  {
    String als_filename_short = new File(als_filename).getName();
    //String xml_filename_short = new File(xml_filename).getName();

    // Make a reporter
    MyReporter rep = new MyReporter();
    
    // Parse and typecheck the model
    Module world = CompUtil.parseEverything_fromFile
      (rep, null, als_filename);

    // Set solving options
    A4Options options = new A4Options();
    options.solver = solver;    
    options.higherOrderSolver = higherOrderSolver;
    
    System.out.printf("Running Alloy, using %s on command %d.\n",
		      options.solver, cmd_index);
    
    // Extract command to execute
    int num_commands = world.getAllCommands().size();
    if (num_commands <= cmd_index) {
      System.out.printf
	("ERROR. File %s contains %d commands; expected at least %d.\n",
	 als_filename_short, num_commands, cmd_index + 1);
      System.exit(1);
    }
    Command command = world.getAllCommands().get(cmd_index);
    
    // Execute the command
    A4Solution soln;
    ConstList<Sig> sigs = world.getAllReachableSigs();
    try {
      soln = TranslateAlloyToKodkod.execute_command
	(rep, sigs, command, options);
    } catch(Exception e) {
      e.printStackTrace(System.out);
      System.exit(1);
      return; // needed to avoid silly compiler error
    }

    Date start = Globals.lastTime;
    Date end = new Date();
    long solveTime = end.getTime() - start.getTime();
    System.out.printf("Solving took %d milliseconds.\n", solveTime);

    if (!iter_flag) {
      if (soln.satisfiable()) {
	String xml_filename =
	  xml_dir + File.separator + "test_0.xml";
	soln.writeXML(xml_filename);
	System.out.printf("%s: Solution saved to %s.\n",
			  getTimestamp(), xml_filename);
	return;
      } else {
	System.out.printf("No solution found.\n");
	return;      
      }
    }
    else {
      int num_solns = 0;
      while (soln.satisfiable()) {
	String xml_filename =
	  xml_dir + File.separator + "test_" + num_solns + ".xml";
	soln.writeXML(xml_filename);
	System.out.printf("%s: Solution %d saved to %s.\r",
			  getTimestamp(), num_solns, xml_filename);	    
	soln = soln.next();
	num_solns++;
      }
      System.out.printf("\n");
      System.out.printf("No more solutions found.\n");
      return;
    }
  }
  
  public static void main(String[] args) throws Err {

    // Get Alloy filename from command line
    if (args.length != 1) {
      System.out.printf("Expected one .als file.\n");
      System.exit(1);
    }
    String als_filename = args[0];

    // Set via "-Dout=<dir>" on the command line.
    String xml_dir = System.getProperty("out");
    if (xml_dir == null) {
      System.out.printf("Expected -Dout=<directory>.\n");
      System.exit(1);
    }

    // Set via "-Dsolver=..." on the command line.
    String solver_str = System.getProperty("solver");
    A4Options.SatSolver solver = null;
    if (solver_str == null) {
      System.out.printf("-Dsolver must be one of [sat4j, cryptominisat, glucose, plingeling, lingeling, minisatprover, minisat].\n");
      System.exit(1);
    }
    if (solver_str.equals("sat4j"))
      solver = A4Options.SatSolver.SAT4J;
    else if (solver_str.equals("cryptominisat"))
      solver = A4Options.SatSolver.CryptoMiniSatJNI;
    else if (solver_str.equals("glucose"))
      solver = A4Options.SatSolver.GlucoseJNI;
    else if (solver_str.equals("plingeling"))
      solver = A4Options.SatSolver.PLingelingJNI;
    else if (solver_str.equals("lingeling"))
      solver = A4Options.SatSolver.LingelingJNI;
    else if (solver_str.equals("minisatprover"))
      solver = A4Options.SatSolver.MiniSatProverJNI;
    else if (solver_str.equals("minisat"))
      solver = A4Options.SatSolver.MiniSatJNI;
    if (solver == null) {
      System.out.printf("-Dsolver must be one of [sat4j, cryptominisat, glucose, plingeling, lingeling, minisatprover, minisat].\n");
      System.exit(1);
    }

    // Set via "-Dhigher=[true/false]" on the command line.
    String higherorder_str = System.getProperty("higherorder");
    boolean higherorder = false;
    if (higherorder_str == null || higherorder_str.equals("false")){
    } else if (higherorder_str.equals("true")){
      higherorder = true;
    } else {
      System.out.printf("-Dhigherorder must be true or false.\n");
      System.exit(1);
    }
    
    // Set via "-Dcmd=..." on the command line.
    String cmd_index_str = System.getProperty("cmd");
    int cmd_index = 0;
    if (cmd_index_str == null) {
      System.out.printf("-Dcmd must be a non-negative integer.\n");
      System.exit(1);
    }
    cmd_index = Integer.parseInt(cmd_index_str);

    // Set via "-Diter=[true/false]" on the command line.
    String iter_str = System.getProperty("iter");
    boolean iter_flag = false;
    if (iter_str == null || iter_str.equals("false")){
    } else if (iter_str.equals("true")){
      iter_flag = true;
    } else {
      System.out.printf("-Diter must be true or false.\n");
      System.exit(1);
    }
    
    runalloy(als_filename, xml_dir, solver,
	     higherorder, cmd_index, iter_flag);

    System.exit(0);    
  }
}

