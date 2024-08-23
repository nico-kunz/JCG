# Dynamic Typing

JavaScript is a dynamically typed language. This means that you don't have to specify the type of any variable when you declare it.
The type will be determined automatically while the program is being processed.

## DT1
[//]: # (MAIN: global)
Test if generator can correctly track the assigned function.

```json
{
  "directLinks": [
    ["<global>", "DT1.two"],
  ],
  "indirectLinks": []
}
```
```js
// dt/DT1.js

function one() {
    return 1;
}

function two() {
    return 2;
}

let x = one;
x = two;

x();
```
[//]: # (END)

## DT2
[//]: # (MAIN: global)
Test if generator can track functions assigned to array

```json
{
  "directLinks": [
    ["<global>", "DT2.two"],
  ],
  "indirectLinks": []
}
```
```js
// dt/DT2.js

function one() {
    return 1;
}

function two() {
    return 2;
}


var x = [one, two];

x[1]();
```
[//]: # (END)