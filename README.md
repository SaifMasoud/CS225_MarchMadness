# CS225-March-Madness
Final Project for CS225 Spring 2017

# Structure of Binary Tree Array
Here are the actual concrete examples of what indecies in the array will map to what level and subtree of an overall tree. All of this applies to a single tree. Each bracket has its own tree, and every tree in the program will have the same structure.

## Indecies and Levels
In this project, we have a tree with 7 levels, expanding out to 64 leaves.
This means we need a total of 127 elements.

```
Winner: 0 (the root of the overall tree)
Final (Ro2): 1 - 2
Final Four: 3 - 6 (roots of division subtrees)
Ro8: 7 - 14
Ro16: 15 - 30
Ro32: 31 - 62
Starting Round (Ro64): 63 - 126
```

## Indecies and Divisions
Each division determines who gets a spot in the Final Four.
The Final Four are indecies 3 through 6.
Thus, by looking at the subtrees starting at these indecies, we can find out which positions in the tree belong to which division.

```
3, 7 - 8, 15 - 18, 31 - 38, 63 - 78
4, 9 - 10, 19 - 22, 39 - 46, 79 - 94
5, 11 - 12, 23 - 26, 47 - 53, 95 - 108
6, 13 - 14, 27 - 30, 54 - 62, 109 - 126
```

## Find Children
Start at index *n*

Index of Left Child: 2 * *n* + 1

Index of Right Child: 2 * *n* + 2

Remember to check if the child is past the end of the array when walking down. Example:

```
Find left child of index 63
Array length: 127
63 * 2 + 1 = 127
127 >= array length: child does not exist.
```

## Find Parent
Start at index *n*

Index of Parent: (*n* - 1) / 2

Integer division will give us the floor of this value.
This gives us the same parent for both children. Example: 

```
Find parent of indecies 63 and 64
(63 - 1) = 62, 62 / 2 = 31
(64 - 1) = 63, 63 / 2 = 31.5 = 31
Parent of 63 and 64 is 31
```

The root of the tree, the element at index 0, technically has itself as its parent. When walking up, check if the current index is 0 - that's when you stop.

## Example: Walk Up from Index 63
```
(63 - 1) = 62; 62 / 2 = 31
(31 - 1) / 2 = 15
(15 - 1) / 2 = 7
(7 - 1) / 2 = 3
(3 - 1) / 2 = 1
(1 - 1) / 2 = 0
```
