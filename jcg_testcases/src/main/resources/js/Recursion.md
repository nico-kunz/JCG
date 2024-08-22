# Recursion
Recursion is a technique in programming where a function calls itself. 

## R1
[//]: # (MAIN: global)
Test the use of recursion in the same module/file.

```json
{
  "directLinks": [
    ["<global>", "R1.factorial"],
    ["R1.factorial", "R1.factorial"]
  ],
  "indirectLinks": []
}
```
```js
// dt/R1.js

// calculate factorial
function factorial(n) {
    if (n === 0) {
        return 1;
    }
    return n * factorial(n - 1);
}

factorial(5);
```
[//]: # (END)

## R2
[//]: # (MAIN: global)
Test the use of two functions that keep calling each other endlessly.

```json
{
  "directLinks": [
    ["<global>", "R2.foo"],
    ["R2.foo", "R2.bar"],
    ["R2.bar", "R2.foo"]
  ],
  "indirectLinks": []
}
```
```js

// af/R2.js

function foo() {
    bar();
}

function bar(y) {
    foo();
}

foo(10);
```
[//]: # (END)