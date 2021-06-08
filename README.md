# LitDB (2021 beta)

(c) Craig Duncan 2017-2021.  

History: PowerDock--> M-Press--> Shelf --> LitDB

Make a request if you wish to use, demonstrate or assist with this project.

# Project folder structure

The app uses these folders for the compiled verson (create if not included)

fxlib : contains the JavaFX library

classes : contains compiled JVM bytecode

# Make sure you have JavaFX library available

This application is built with JDK16 and with the support of the JavaFX modules (version JavaFX16, 2020).

The JavaFX is no longer included with the Java SDK by default, so for development I needed to download the JavaFX library.  I rename the /lib folder in the javafx-sdk-16 (or similar) to fxlib and include it in the root folder of my project.

see 
JDK16: 		https://jdk.java.net/16/release-notes
JavaFX16: 	https://openjfx.io and https://gluonhq.com/products/javafx/

The openJDK download is specific for each OS.  The SDK library file when unzipped will be closer to 95MB.

# Compiling and running from source

With JavaSE 16 installed, the command line (shell) commands to execute to run the program include commands to make sure JavaFX modules are identified.   

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

# Application specific help 

To begin, press CMD-N for new box/book and then do File-->Save As (CMD-SHIFT-S).

You can drag books/boxes to different locations in the grid workspace.

You can also CMD-C and CMD-V to copy and paste existing boxes.

Press DEL on box/book with focus to delete.

CMD-W to close the workspace (will not save).  To save first, CMD-S.

Markdown file will save in same order as boxes (reading across rows left to right).

# Open a markdown, R markdown or docx file

Alternatively, to work with an existing markdown document:

File --> Open MD Document.

It will detect # divisions in a text/markdown file and interpret those as book divisions.

If file has previously been saved with this app, it will re-open the box positions as saved.

# Save As Docx

Save as docx will create a docx from the markdown text (currently it does not recycle a docx file opened in the application).

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


