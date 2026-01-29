# Higher Order Functions
Higher Order Functions (HOF) are functions that take other functions as arguments or return functions as results.

## HOF1
[//]: # (MAIN: global)
Test the use of a function as an argument to another function.

```json
{
  "directLinks": [
    ["HOF1.foo", "HOF1.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF1.py

def foo(f):
    f(1)

def bar(x):
    return x

foo(bar)
```
[//]: # (END)

## HOF2
[//]: # (MAIN: global)
Test the use of a function as a return value of another function.

```json
{
  "directLinks": [
    ["<global>", "HOF2.foo"],
    ["<global>", "HOF2.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF2.py

def foo():
    return bar

def bar(x):
    return x

foo()(1)
```
[//]: # (END)

## HOF3
[//]: # (MAIN: global)
Test the use of a function as return value but stored in variable

```json
{
  "directLinks": [
    ["<global>", "HOF3.foo"],
    ["<global>", "HOF3.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF3.py

def foo():
    return bar

def bar(x):
    return x

f = foo()
f(1)
```
[//]: # (END)

## HOF4
[//]: # (MAIN: global)
Test reassignment of a function to a variable

```json
{
  "directLinks": [
    ["<global>", "HOF4.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF4.py

def foo():
    return bar

def bar(x):
    return x

f = foo
f = bar
f(1)
```
[//]: # (END)

## HOF5
[//]: # (MAIN: global)
Test call to returned function that was passed as argument.

```json
{
  "directLinks": [
    ["<global>", "HOF5.bar"],
    ["<global>", "HOF5.foo"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF5.py

def bar(func):
    return func

def foo():
    return 1

bar(foo)()
```
[//]: # (END)

## HOF6
[//]: # (MAIN: global)
Test if chained assignments are correctly tracked.

```json
{
  "directLinks": [
    ["<global>", "HOF6.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF6.py

def bar():
    return 1

f = g = bar
f()
```
[//]: # (END)

## HOF7
[//]: # (MAIN: global)
Test if multi assignments are correctly tracked.

```json
{
  "directLinks": [
    ["<global>", "HOF7.foo"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF7.py

def bar():
    return 1
    
def foo():
    return 2

f, g = bar, foo
g()
```
[//]: # (END)

## HOF8
[//]: # (MAIN: global)
Test reassignment of a function to a variable and then use as argument
```json
{
  "directLinks": [
    ["<global>", "HOF8.foo"],
    ["HOF8.foo", "HOF8.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF8.py

def foo(func):
    func(1)
    
def bar(x):
    return x
    
f = bar
foo(f)
```
[//]: # (END)

## HOF9
[//]: # (MAIN: global)
Test call to function as default parameter.
```json
{
  "directLinks": [
    ["<global>", "HOF9.foo"],
    ["HOF9.foo", "HOF9.bar"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF9.py

def bar(x):
    return x

def foo(f = bar):
    f(1)

foo()
```
[//]: # (END)

## HOF10
[//]: # (MAIN: global)
Test assignment to starred variable.
```json
{
  "directLinks": [
    ["<global>", "HOF10.func2"],
    ["<global>", "HOF10.func3"]
  ],
  "indirectLinks": []
}
```
```python
# hof/HOF10.py
def func1():
    pass

def func2():
    pass

def func3():
    pass

def func4():
    pass

a, *b, c = func1, func2, func3, func4

b[0]()
b[1]()
```
[//]: # (END)
