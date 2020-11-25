# CS321-B-Tree
### Team Programming Project: Bioinformatics


## Overview:
This program, given a valid .gbk (GenBank Data File) and a set of command line arguments, parses through the file and inserts gene sequences into our BTree implementation. In order to save memory, the BTree's data is stored on the user's disk and, in order to make insertions/searches faster, allows the user to use our cache implementation. In addition to the option to create a BTree, users can also search a specified BTree for sequences of a given length.

## Usage:
### GeneBankCreateBTree
In order to create a BTree, compile the program by typing...
```
javac GeneBankCreateBTree.java
```
... and enter the following arguments...
```
java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]
```
... where cache must be 0 or 1, degree is at least 3, gbk file must be a valid format, and sequence length is at least 1. If cache is being utilized, the cache size must be at least 1. If debug level is being used, it must be either 0 or 1.

### GeneBankSearch
In order to search an existing BTree file, compile the program by typing...
```
javac GeneBankSearch.java
```
... and enter the following arguments...
```
java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]
```
... where cache must be 0 or 1, btree file must be a valid format, and query file is a valid format. If cache is being utilized, the cache size must be at least 1. If debug level is being used, it must be either 0 or 1.

## Metadata/BTree File Layout
In our implementation of BTree, both our Metadata and BTree files are stored in a single, auto-generated file.

### BTree Node Structure
The BTree Node's structure in a BTree file is expressed like this:
```
{[Object][Object]...[Object]}, {[Long childPointer][Long childPointer]...[Long childPointer]}, [int numKeys], [boolean isLeaf], [long filePosition]
```

Every Object within a BTree Node contains the following:
```
[long dnaSequence], [int frequency]
```

### Read/Write Methods
Whenever information needs to be written or read from our file, we call the public void methods "diskRead(long filePos)" and "diskWrite(BtreeNode node)".

When diskRead is called, the method creates a new BTreeNode, creates a new RandomAccessFile, seeks to the specified position, and uses for-loops to collect data from the seeked position/pass it into the new BTreeNode. The first for-loop sets the node's keys[i].sequence value equal to RandomAccessFile.readLong(), then sets the node's keys[i].frequency value to RandomAccessFile.readInt() value. A second for-loop sets the node's children[i] value equal to RandomAccessFile.readLong(). Finally, the node's numKeys, isLeaf, and filePosition values are set to RandomAccessFile.readLInt(), RandomAccessFile.readBoolean(), and RandomAccessFile.readLong() respectively.

When diskWrite is called, the method creates a new RandomAccessFile and seeks to the specified node's filePosition. From there, a for-loop writes all of the the node's keys[i].sequence long values and keys[i].frequency int values. Another for-loop is used to write all of the node's children[i] long values. Finally, the node's numKeys (int), isLeaf (boolean), and filePosition (long) values are written to the file.

### Other Notes
It should be noted that our implementation of BTree utilizes BTree nodes that are always full of TreeObjects. However, TreeObjects are marked as "empty" by setting their sequence to "-1" and their frequency to "0". This way, our read/write methods don't have to worry about skipping spaces in memory to account for "empty" BTree key indexes. A similar approach is employed for non-existent child pointers. All of this is handled by the program itself.

In addition, when the user decides to utilize a cache, objects are both written to the user's disk and inserted into the cache when diskWrite is called, but calling diskRead will first check the cache and only search the disk if there was no "hit".
