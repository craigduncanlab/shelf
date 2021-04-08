# Shelf (2020 beta)

(c) Craig Duncan 2017-2020.  

History: PowerDock--> M-Press--> Shelf

Make a request if you wish to use, demonstrate or assist with this project.

# Project folder structure

The *Shelf* app uses these folders (create if not included)

src: contains Java source files

fxlib : contains the JavaFX library

classes : contains compiled JVM bytecode

content : contains markdown files for input/output

buildnotes: contains notes on Design, Classes.

# Make sure you have JavaFX library available

To use JavaFX you need to invoke it as a module when you compile and run.  It is no longer included with the Java SDK.

So first you need to download the JavaFX library.

see https://openjfx.io and https://gluonhq.com/products/javafx/

The openJDK download is specific for each OS.  The SDK library file when unzipped will be closer to 95MB.

When you have downloaded the JavaFX SDK package, locate the folder javafx-sdk-11.0.2 (or similar).

Inside the downloaded javafx package, find the lib folder with the javafx .jar files in it.  There are also dylib files, which are needed too, but they are too large to store here.

Rename the 'lib' folder to fxlib and put it into the root folder of the Shelf project.

# Compiling and running from source

The next step is to set up the environment variables and make it easier to compile and run the program.  This requires some terminal (bash) commands to be executed, in order.

In the parent folder (the root folder for the local copy of the github repository files):

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

# Running from archive

An archive has byte-code compiled class files in it, ready to run as an executable on a java machine.

Create a manifest file (editor.mf) with the name of the Main-Class in it.

"Main-Class:classname that defines the class with the public static void main(String[] args) method that serves as your application's starting point."

Issues: a JavaFX application may not have a main method, because it could be :

@override
public void start(Stage primaryStage) 

or it could be:

public class Main extends Application

The archive has a manifest file that will contain, at least, the first (main) class file to execute from the java archive (.jar) file.  However the advantage of a manifest file is that you can include your 'jar' options in it, so that you don't have to type them on the command line.  Instead, you include the link to the manifest file.

If the application contains references to existing jar files (modules), as mine does (swing,control, web), then how to you incorporate these?  'controls' is the largest at 2.5MB.

The archive was created from source using this jar command (with options cfmp i.e. create, file, manifest, then path for class files, path for existing .jar modules:

(before you run this you compile in the normal way to create your class files - the .java files are not needed for the archige)

jar options include:

-c is create
-C is change directory and include files following (i.e. the .class)

You can do this on command line, and include A.class B.class explicitly, or use the -C command to include a directory containing the files

jar --create --file 2Deditor.jar --manifest editor.mf -C class/

Does it help to put relevant .jar files in that directory too?
e.g. javafx.swing.jar

What if your execute command is more complicated?  Can you include in your archive file?
You can include @file after jar command and put your jar options in a text file.
e.g.
jar --create --file my.jar @classes.list


runprog='java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.swing,javafx.web -cp classes Main'

The above 'run' uses the javafx modules that are already compiled and sitting in the PATH_TO_FX, which in my case is 'fxlib'.

The jar command will accept -p or '--module-path' so that's okay.
In this case, 

What about 'add modules'?

if you specify the file with the name 'module-info.class' in both the jar file creation line and then include descriptions of the modules you need in that file.

http://tutorials.jenkov.com/java/modules.html#modules-contain-one-or-more-java-packages


# Start

To begin, press CMD-N for new box/book and then do File-->Save As (CMD-SHIFT-S).

You can drag books/boxes to different locations in the grid workspace.

You can also CMD-C and CMD-V to copy and paste existing boxes.

Press DEL on box/book with focus to delete.

CMD-W to close the workspace (will not save).  To save first, CMD-S.

Markdown file will save in same order as boxes (reading across rows left to right).

# Open a markdown file

Alternatively, to work with an existing markdown document:

File --> Open MD Document.

It will detect # divisions in a text/markdown file and interpret those as book divisions.

If file has previously been saved with this app, it will re-open the box positions as saved.

# Importing

File-->Import as new Row.

Loads a markdown document as a row.  Alternative to File-->Open which will keep the positions previously saved.

# General commands

| KeyCode | Action | 
| :------------- | :---------- | 
| CMD-O |Open a new bookshelf file (.md)|
| CMD-N |Put a new book on bookshelf |
| CMD-C | Copy selected Book to clipboard |
| DELETE | Delete box with focus |
| CMD-V | Paste clipboard as New Book |
| CMD-S | Save bookshelf data (to current file)|
|       | This saves all book positions on shelf, and text file is written in shelved order|
| CMD-W | Clear the bookshelf from memory (Close).
| CMD-E | Nudge focus box left |
| CMD-R | Nudge focus box right |
| LEFT | Move focus to previous box |
| RIGHT | Move focus to next box |
| **Views** |
| CMD-I | Edit/Inspect a book's metadata |
| SPACE | Open HTML text preview (press SPACE again to close it) |

# In the Editor/Inspector

| KeyCode | Action | 
| :------------- | :---------- | 
| CMD-S | General save |
| CMD-W | Close inspector |
| CMD-Z | Toggle grid/inspector/preview |


