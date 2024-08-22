# Modules
JS modules allow you to split your code into multiple files. This can help keep code organized and maintainable. 

## M1
[//]: # (MAIN: global)
Test the use of a named import.

```json
{
  "directLinks": [
    ["<global>", "M1.foo"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M1.js

export function foo(x) {
    return x + 1;
}
```
```js
//modules/Main.js
import { foo } from './M1.js';

foo(1);
```
[//]: # (END)

## M2
[//]: # (MAIN: global)
Test the use of a named import with an alias name.
```json
{
  "directLinks": [
    ["<global>", "M2.foo"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M2.js

export function foo(x) {
    return x + 1;
}
```
```js
//modules/Main.js

import { foo as myFoo } from './M2.js';

myFoo(1);
```
[//]: # (END)

## M3
[//]: # (MAIN: global)
Test the use of a default import.
```json
{
  "directLinks": [
    ["<global>", "M3.foo"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M3.js

export default function foo(x) {
    return x + 1;
}
```
```js
//modules/Main.js

import foo from './M3.js';

foo(1);
```
[//]: # (END)

## M4
[//]: # (MAIN: global)
Test the use of mixed default and named imports.
```json
{
"directLinks": [
    ["<global>", "M4.foo"],
    ["<global>", "M4.bar"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M4.js

export default function foo(x) {
    return x + 1;
}

export function bar(x) {
    return x + 2;
}
```
```js
//modules/Main.js

import foo, { bar } from './M4.js';

foo(1);
bar(2);
```
[//]: # (END)

## M5
[//]: # (MAIN: global)
Test default import with a different name.
```json
{
  "directLinks": [
    ["<global>", "M5.foo"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M5.js

export default function foo(x) {
    return x + 1;
}
```
```js
//modules/Main.js

import myFoo from './M5.js';

myFoo(1);
```
[//]: # (END)

## M6
[//]: # (MAIN: global)
Test the use of namespace import.
```json
{
  "directLinks": [
    ["<global>", "M6.foo"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M6.js

export function foo(x) {
    return x + 1;
}
```
```js
//modules/Main.js

import * as myModule from './M6.js';

myModule.foo(1);
```
[//]: # (END)

## M7
[//]: # (MAIN: global)
Test the use of a CommonJS import.
```json
{
  "directLinks": [
    ["<global>", "M7.foo"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M7.js

module.exports = function foo(x) {
    return x + 1;
}
```
```js
//modules/Main.js

const foo = require('./M7.js');

foo(1);
```
[//]: # (END)

## M8
[//]: # (MAIN: global)
Test chained imports
```json
{
  "directLinks": [
    ["<global>", "One.first"],
    ["One.first", "Two.second"]
  ],
  "indirectLinks": []
}
```
```js
// modules/Main.js

import { first } from './One.js';

first();
```
```js
// modules/One.js

import { second } from './Two.js';

export function first() {
    second();
}
```
```js
// modules/Two.js

export function second() {
}
```
[//]: # (END)

## M9
[//]: # (MAIN: global)
Test import all
```json
{
  "directLinks": [
    ["<global>", "M9.foo"],
    ["<global>", "M9.bar"]
  ],
  "indirectLinks": []
}
```
```js
// modules/M9.js

export function foo(x) {
    return x + 1;
}

export function bar(x) {
    return x + 2;
}
```
```js
//modules/Main.js

import * as myModule from './M9.js';

myModule.foo(1);
myModule.bar(2);
```
[//]: # (END)
