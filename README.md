# DuplicateFileFinder
DuplicateFileFinder is a **Java** app used to find all duplicate files in a given input Zip folder.

## Problem description
A client merged their home photo folder with their partner's folder, with disastrous consequences. They now have a lot of duplicate photos in different places in the folder structure. They've provided a .zip of this photos folder.

Write a command-line program which finds files which have exactly the same contents and outputs any duplicates (and their locations) to standard output.
 

Consider the the following points in formulating your solution:

* What if this same solution was used on a really large set of photos? What if it was a thousand photos? Or tens of thousands?
* What if this was a three-way merge, with triplicates? Does your solution account for this?
* Some of these files may have had their filename changed.
* Some of these may have only their extension changed.

 
## Assumptions
* Currently supports only Zipped folder containing files as input
* Looks for duplicate contents of files regardless of file extensions or file names, thereby not only supporting photos but also text files
* Takes the client provided zip as default input to program - optionally, a different zip file can be provided as command line argument

## How to run the app

### Pre-requisites

* Maven 3.1+
* Java 11+

### Step by step instructions

Run the below commands from terminal after cloning this repository:
1. mvn package
2. Run the below command to execute the program with the file path as input: 
```java -cp target/DuplicateFileFinder-1.0-jar-with-dependencies.jar com.finder.Main src/main/resources/Code_Test.zip```

One optional argument to com.print.Main program:
* Path to Zip file with photos (src/main/resources/Code_Test.zip in this example)
By default, if no input is given, [the default file provided](src/main/resources/Code_Test.zip) is automatically picked up by the program

## Solution

### Implementation and key design decisions

* **FilesProcessor** abstract class represents a generic files processor that stores a hashmap (of checksum of file contents and list of filenames matching the checksum)
* **FileProcessing** is an interface of file operations that is implemented by _FilesProcessor_
  * **ZipFilesProcessor** is a concrete implementation of _FilesProcessor_ and implements Zip specific processing of unpacking given zip folder in _processFiles_. Also contains special zip file specfic validations in _checkZipFileEntry_.
  * If this program needs to support new file formats, say .tar then a new class should be created, say TarFilesProcessor, that will extend from _FilesProcessor_
* **FilesProcessorFactory** class helps create concrete _FileProcessor_ objects based on input file (i.e, if the input is a ".zip" file, it would initialize **ZipFilesProcessor**). This was designed this way to keep it easily extensible for other file extensions later on and the **DuplicateFileFinder** class does not need to be aware of specific file type classes.
* **Checksum** class helps calculate hash checksum of given bytes. 
  * For different files with same contents, the checksum will be the same for the file contents. This is used to help identify duplicate file contents.
  * _FilesProcessor_ class takes in a **Checksum** object as input to calculate the checksum value during processing of each file entry and stores it in _hashedFileMap_ hashmap.
* **DuplicateFileFinder** class is the entry point and core program that takes in the source file path as input and identifies duplicate files
  * Initialises SHA-256 _Checksum_ object and creates a new **FilesProcessor** object based on input file type (using **FilesProcessorFactory**)
  * Filters the hashmap from _FilesProcessor_ to filter and display duplicate files (if there is more than one file for a given checksum, they are duplicates).
