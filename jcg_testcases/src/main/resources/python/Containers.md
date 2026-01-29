# Containers
In Python, containers may hold functions as values. These tests check how the functions are tracked when they are stored in different types of containers.

## CO1
[//]: # (MAIN: global)
Test if a list of functions is correctly tracked.

```json
{
  "directLinks": [
    ["<global>", "CO1.foo"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO1.py

def bar():
    return 1

def foo():
    return 2

f = [bar, foo]
f[1]()
```
[//]: # (END)

## CO2
[//]: # (MAIN: global)
Test if a dictionary of functions is correctly tracked.

```json
{
  "directLinks": [
    ["<global>", "CO2.foo"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO2.py

def bar():
    return 1

def foo():
    return 2

m = {"a": bar, "b": foo}
m["b"]()
```
[//]: # (END)

## CO3
[//]: # (MAIN: global)
Test if pop function is correctly handled.

```json
{
  "directLinks": [
    ["<global>", "CO3.foo"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO3.py

def bar():
    return 1

def foo():
    return 2

s = [bar, foo]
s.pop()()
```
[//]: # (END)

## CO4
[//]: # (MAIN: global)
Test if a tuple of functions is correctly tracked.

```json
{
  "directLinks": [
    ["<global>", "CO4.bar"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO4.py

def bar():
    return 1

def foo():
    return 2

t = (bar, foo)
t[0]()
```
[//]: # (END)

## CO5
[//]: # (MAIN: global)
Test if dictionary key assignment is correctly tracked.

```json
{
  "directLinks": [
    ["<global>", "CO5.foo"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO5.py

def bar():
    return 1

def foo():
    return 2

d = {}
d["f"] = foo
d["f"]()
```
[//]: # (END)

## CO6
[//]: # (MAIN: global)
Test if dictionary update method is handled correctly.

```json
{
  "directLinks": [
    ["<global>", "CO6.bar"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO6.py

def bar():
    return 1

def foo():
    return 2

d = {}
d.update({"f": bar})
d["f"]()
```
[//]: # (END)

## CO7
[//]: # (MAIN: global)
Test if nested lists are handled.
```json
{
  "directLinks": [
    ["<global>", "CO7.bar"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO7.py

def bar():
    return 1

def foo():
    return 2

f = [[],[bar, foo]]
f[1][0]()
```
[//]: # (END)

## CO8
[//]: # (MAIN: global)
Test if nested dictionaries are handled.

```json
{
  "directLinks": [
    ["<global>", "CO8.foo"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO8.py

def bar():
    return 1

def foo():
    return 2

m = {"outer": {"a": bar, "b": foo}}
m["outer"]["b"]()
```
[//]: # (END)

## CO9
[//]: # (MAIN: global)
Test use of slice.

```json
{
  "directLinks": [
    ["<global>", "CO9.foo"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO9.py

def bar():
    return 1

def foo():
    return 2

f = [bar, foo, 1, 2, 3]
f_slice = f[0:2]
f_slice[1]()
```
[//]: # (END)

## CO10
[//]: # (MAIN: global)
Test use of list comprehension.

```json
{
  "directLinks": [
    ["<global>", "CO10.bar"]
  ],
  "indirectLinks": []
}
```
```python
# co/CO10.py

def bar():
    return 1

def foo():
    return 2
f = [bar, foo, bar, foo]
f_comp = [func for func in f if func == bar]
f_comp[1]()
```
[//]: # (END)
