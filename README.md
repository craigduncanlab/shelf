# LitDB (2021 beta)

(c) Craig Duncan 2017-2021.  

What does the sofware do?  It provides an integrated visual text analysis tool for exploring the visual (GUI) structure of your text documents, and their internal XML structures (style codes and bookmarks in the case of Word).  The structures will likely have been encoded by the authors using the native GUI application, and may or may not form a sensible database schema. 

It is intended to be interoperable across different text formats or text annotations, including markdown, HTML and OOXML (docx, odf).

I am writing a more academic paper on its design and potential uses, available in due course.  

The Project names have progressed through several beta versions (flavours) so far, including PowerDock, M-Press, Shelf and now LitDB.  I am predominantly developing on a mac, but it will be suitable for cross-platform distribution.

# Application specific help 

This is still a beta project and full documentation and instructions are yet to be published here.

## General

In general, the workflow is:
1. Application will open docx, md and rmd formats.  
2. It will display these according to their high-level structure. [Choose Split to vary the docx splitting option]
3. The visual tab will allow users to change the position, order or display of the boxed content of the original file.
4. Editing or inspecting content is achieved by selecting a box, then pressing enter.
5. Pressing spacebar when a box is selected will show an HTML preview of the content of that box. Notes are in different style.
6. Some edits made in the program can be saved to a new file, or to a different format.  This is in development (see below).

## Markdown input files

1. Markdown files are broken up according to level 1 hash (#).  Use 'Display' menu to change appearance in Viewer.
2. Notes/code can be inspected inside blocks by pressing Enter after selecting block in main viewer. 
3. Markdown can be edited and re-saved.  At present, this retains the GUI layout as additional coding in the file.
4. It is possible to export markdown content into a structure set of web pages.  This function has been developed, it will be added to GUI menus in near future.
5. Select one of the boxes in the visual display, then press enter to inspect.  Markdown can be edited here.  
6. Save file when in main app view to capture any changes to the markdown file.

## OOXML/docx files:

1. Docx files are broken up visually according to outline level 1.  Use 'Display' menu to change appearance in Viewer.
2. In the application, the database concept of Fields is used, and is intended to work with Outline level 1 (Split option=Outline).  
3. Files are visualised in the first tab on the RHS.  
   To adopt a different 'block' scheme, choose Split to vary the docx splitting option.  You may need to select a different Display option after changing the Splitting scheme.
4. A summary of the style and field information appears on the second to fourth RHS tabs.
5. To add fields to docx, use fields menu.  This is specific to context.  This is in progress.
These are not retained in the original docx file unless the Update Docx menu item, from the File Menu, is used.  A new filename can be used at that stage.
6. Select one of the boxes in the visual display, then press enter to inspect. THE OOXML text imported on a docx open is currently being stored in both OOXML and markdown slots but this will be reviewed in next update.
7. Save, Save As functions are not yet in place, but will eventually allow some docx changes to be saved into original document, or document with new name.

## Interchangable formats

'Save as docx' will create a docx from the markdown text (currently it does not recycle a docx file opened in the application).

# General keyboard commands

THe GUI now has several new features but this is a guide as to functions that will work.  You can also select multiple cells using SHIFT and mouse clock, or COMMAND and mouse click.

| KeyCode | Action | 
| :------------- | :---------- | 
| CMD-O |Open a new bookshelf file (.md)|
| CMD-N |Put a new book/icon on workspace |
| CMD-C | Copy selected Book to clipboard |
| DELETE | Delete box with focus |
| CMD-V | Paste clipboard as New Book |
| CMD-S | Save bookshelf data (to current file)|
|       | This saves all book positions on shelf, and text file is written in shelved order|
| CMD-W | Clear the current document from memory (Close).
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

# Install from packages/executable

--- COMING SOON ---

# Install from source

The app uses these folders for the compiled verson (create if not included)

fxlib : contains the JavaFX library

classes : contains compiled JVM bytecode

## Make sure you have JavaFX library available

This application is built with JDK16 and with the support of the JavaFX modules (version JavaFX16, 2020).

The JavaFX is no longer included with the Java SDK by default, so for development I needed to download the JavaFX library.  I rename the /lib folder in the javafx-sdk-16 (or similar) to fxlib and include it in the root folder of my project.

see 
JDK16: 		https://jdk.java.net/16/release-notes
JavaFX16: 	https://openjfx.io and https://gluonhq.com/products/javafx/

The openJDK download is specific for each OS.  The SDK library file when unzipped will be closer to 95MB.

## Compiling and running from source

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



