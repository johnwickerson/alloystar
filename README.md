Alloy* (source code mirror)
===================================

Summary
-------
This is a copy of the source code for [MIT's Alloy* model checking tool](http://alloy.mit.edu/alloy/hola/).
It also includes an Ant build.xml script, which is not part of the original MIT source code.
This copy was created to facilitate modification to the core Alloy* tool (the parts which fall
under the `edu.mit` package structure). 

This copy of the Alloy* tool has been produced in the same way that copy of the original Alloy tool was produced by Andre Beckus (https://github.com/beckus/AlloyAnalyzer).

It was created as follows (not necessarily in this order):

1. Downloaded the JAR file located at: http://alloy.mit.edu/alloy/hola/downloads/hola-0.2.jar
2. Extracted the JAR file.
3. Added this `README.md` file and a `build.xml` file.
3. Deleted core `.class` files (using the _clean_ target in `build.xml`)

Building
--------
The Ant build.xml script contains the following targets:

-   _build_: Compiles the `.java` files under the `edu` directory.

    Other directories are not touched; it is assumed that these contain libraries
    which have been pre-compiled.

    The auto-generated parser and lexer `.java` files (located in the `edu/mit/csail/sdg/alloy4compiler/parser` directory)
    are neither deleted nor generated by the Ant script.  The directory already contains shell scripts
    to re-generate them using JFlex and CUP.
-   _dist_: Creates an executable JAR file in the `dist` directory.  This JAR file looks essentially like the official
    Alloy JAR file released by MIT.
-   _all_: Runs _dist_.
-   _clean_: Deletes the `dist` directory and all class files under the `edu` directory.

Notes
-----

-   As per the manifest, the main class is `edu.mit.csail.sdg.alloy4whole.SimpleGUI`.
-   The version number and build date which the tool displays are not accurate.
    These are set in the `edu.mit.csail.sdg.alloy4.Version` class, and are supposed to be
    updated by the build script when building a release.
    This project was not intended to create official releases, so it was left as-is.
-   There is a class `edu.mit.csail.sdg.alloy4.MailBug` which includes logic to email
    crash reports to MIT.  You should change this class if you are modifying the source code
    and creating your own release.
