# Classes

## C1
[//]: # (MAIN: global)
Test the call to a class constructor.

```json
{
  "directLinks": [
    ["<global>", "C1.constructor:3"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C1.js

class Foo {
    constructor(x) {
        this.x = x;
    }  
    
    test() {
        return 1;
    }
}

const f = new Foo(1);
```
[//]: # (END)

## C2
[//]: # (MAIN: global)
Test a class method call.

```json
{
  "directLinks": [
    ["<global>", "C2.test:7"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C2.js

class Foo {
    constructor(x) {
        this.x = x;
    }

    test() {
        return 1;
    }
}

const f = new Foo(1);
f.test();
```
[//]: # (END)

## C3
[//]: # (MAIN: global)
Test a class method calling another class method on self.

```json
{
  "directLinks": [
    ["C3.bar", "C3.foo:7"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C3.js

class Foo {
    constructor(x) { 
        this.x = x;
    }

    foo() {
        return 1;
    }

    bar() {
        this.foo();
    }
}

const f = new Foo(1);
f.bar();
```
[//]: # (END)

## C4
[//]: # (MAIN: global)
Test a call to inherited method.

```json
{
  "directLinks": [
    ["<global>", "C4.foo:7"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C4.js

class Foo {
    constructor(x) {
        this.x = x;
    }

    foo() {
        return 1;
    }
}

class Baz {
    constructor(x) {
        this.x = x;
    }
    
    foo() {
        return 2;
    }
}

class Bar extends Foo {
    constructor(x) {
        super(x);
    }
}

const f = new Bar(1);
f.foo();
```
[//]: # (END)

## C5
[//]: # (MAIN: global)
Test method calling method of superclass.

```json
{
  "directLinks": [
    ["C5.foo:18", "C5.foo:7"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C5.js

class Foo {
    constructor(x) {
        this.x = x;
    }

    foo() {
        return 1;
    }
}

class Bar extends Foo {
    constructor(x) {
        super(x);
    }

    foo() {
        super.foo();
        return 2;
    }
}

const f = new Bar(1);
f.foo();
```
[//]: # (END)

## C6
[//]: # (MAIN: global)
Test a call to super constructor.

```json
{
  "directLinks": [
    ["C6.constructor", "C6.constructor:3"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C6.js

class Foo {
    constructor(x) {
        this.x = x;
    }
}

class Bar extends Foo {
    constructor(x, y) {
        super(x);
        this.y = y;
    }
}

const f = new Bar(1, 2);
```
[//]: # (END)

## C7
[//]: # (MAIN: global)
Test class constructor taking function and initializing member with the function. Then call the function via the member.

```json
{
  "directLinks": [],
  "indirectLinks": ["C7.bar"]
}
```
```js
// classes/C7.js

class Foo {
    constructor(f) {
        this.f = f;
    }

    callF(x) {
        return this.f(x);
    }
}
function bar(x) {
    return x + 1;
}

const f = new Foo(bar);
f.callF(1);
```
[//]: # (END)

## C8
[//]: # (MAIN: global)
Test class taking default parameter for constructor.

```json
{
  "directLinks": [],
  "indirectLinks": ["C8.bar"]
}
```
```js
// classes/C8.js
function bar(x) {
    return x + 1;
}
class Foo {
    constructor(f = bar) {
        this.f = f;
    }

    callF(x) {
        return this.f(x);
    }
}
const f = new Foo();
f.callF(1);
```
[//]: # (END)

## C9
[//]: # (MAIN: global)
Test error class being thrown.

```json
{
  "directLinks": [
    ["<global>", "C9.constructor"]
  ],
  "indirectLinks": []
}
```
```js
// classes/C9.js

class CustomError extends Error {
    constructor(message) {
        super(message);
        this.name = "CustomError";
    }
}

throw new CustomError("This is a custom error");
```
[//]: # (END)