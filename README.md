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

# Setup

Default:

```
 String projectfolder = "";  //This is the current top level folder with the src and fxlib folders in it
 String templatesfolder = projectfolder+"/templates/";
 String recentsfolder = projectfolder+"/config/";
```
e.g. with src folder for source files etc

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

# --- Simple instructions ---

# Bookshelf

## Open previously created Shelf (markdown format)

Choose File --> Open MD Document.

This will open up a markdown document.  It will detect # divisions in a text file and interpret those as book divisions.

Books will be displayed on the shelf, and you can drag them to different locations.

## New Books

Choose Book--> New to add a new Book to Bookshelf.

## Bookshelf view key commands:

CMD-O: Open a new bookshelf file (.md)

CMD-N: Put a new book on bookshelf.

CMD-C: Copy selected Book to clipboard.

DELETE: Delete currently selected book.

CMD-V: Paste clipboard as New Book to bookshelf.

CMD-S: Save bookshelf data (to current file).  This saves all book positions on shelf, and text file is written in shelved order.

CMD-W: Clear the bookshelf from memory (Close).

## View options

CMD-I: Inspect a book's metadata

SPACE: Open Book's HTML text preview (press SPACE again to close it).

# Book Inspector

## Book metadata view key commands

CMD-S: Save the updated book metadata (and saves all other bookshelf data)

CMD-W or CMD-I: Close the metadata view.

CMD-Z: Toggles the view to show/hide the HTML preview.

