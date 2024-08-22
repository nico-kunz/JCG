# Dynamic Code Execution

## DCE1
[//]: # (MAIN: global)
Test a simple function call through eval.

```json
{
  "directLinks": [],
  "indirectLinks": [["<global>", "DCE1.foo"]],
}
```
```js
// af/DCE1.js

function foo() {
    return 1;
}

eval('foo()');
```

## DCE2
[//]: # (MAIN: global)
Test a simple function call to a function defined in a string.

```json
{
  "directLinks": [],
  "indirectLinks": [["<global>", "DCE2.foo"]],
}
```
```js
// af/DCE2.js

let code = "function foo() { return 1; } foo();";

eval(code);
```