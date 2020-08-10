# M-Press (2020 beta, fork of powerdock)

(c) Craig Duncan 2017-2020.  Make a request if you wish to use, demonstrate or assist with this project.

# Project folder structure

The *mpress* app uses these folders (create if not included)

src: contains Java source files

fxlib : contains the JavaFX library

classes : contains compiled JVM bytecode

content : contains markdown files for input/output

buildnotes: contains notes on Design, Classes.

# Setup

Modify the Config.java file before compiling to ensure project directory is specified.

Default:

```
 String projectfolder = "";  //This is the current top level folder with the src and fxlib folders in it
 String templatesfolder = projectfolder+"/templates/";
 String recentsfolder = projectfolder+"/config/";
```
e.g. with src folder for source files etc

# Make sure you have JavaFX library available

To use JavaFX you need to invoke it as a module when you compile and run.  It is not longer automatically part of the Java SDK.

So first you need to download the JavaFX library.

see https://openjfx.io and https://gluonhq.com/products/javafx/

The openJDK download is specific for each OS.  The SDK library file when unzipped will be closer to 95MB.

When you have downloaded the JavaFX SDK package, locate the folder javafx-sdk-11.0.2 (or similar) and inside this, find the lib file with the javafx .jar files in it.  You will also need the dylib files, but they are too large to store here.

Rename it to fxlib and put it into the root folder of the Powerdock project.

# Compiling and running from source

The next step is to set up the environment variables and make it easier to invoke the program.  This requires some terminal (bash) commands to be executed, in order.

In the parent folder (the root folder for the github):

```
export PATH_TO_FX='fxlib'
alias compilej='javac -cp classes -d classes src/*.java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.swing,javafx.web'
alias runprog='java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.swing,javafx.web -cp classes Main'
```

Then just type this to compile:

```
compilej 
```
and this to run:

```
runprog
```

# Simple instructions

## Open Markdown document

Choose File --> Open MD Document.

This will open up a markdown document inside a block container(s).

As markdown parsing functions are added, this will parse the markdown file and divide into appropriate boxes.

## New Blocks

Choose New and Block to create a new container for a markdown heading 1 block.

