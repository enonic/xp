# URL Parameter Serialization - Nested Objects Support

## Overview

The URL parameter serialization in `serviceUrl`, `pageUrl`, `assetUrl`, and other portal URL functions now supports nested objects and arrays. Complex objects are automatically serialized as JSON strings.

## Previous Behavior

Before this change, nested objects were converted using Java's `.toString()` method, which produced unparseable string representations:

```javascript
// Before
const params = {
    data: [{name: "a"}, {name: "b"}]
};
const url = portal.serviceUrl({service: 'myservice', params});
// Result: ?data={name=a}&data={name=b}  // Cannot be parsed back to objects
```

## New Behavior

Nested objects and arrays are now serialized as proper JSON:

```javascript
// After
const params = {
    data: [{name: "a"}, {name: "b"}]
};
const url = portal.serviceUrl({service: 'myservice', params});
// Result: ?data={"name":"a"}&data={"name":"b"}  // Valid JSON!

// In the service handler:
exports.get = function(req) {
    // Parse the JSON parameter
    const data = req.params.data.map(item => JSON.parse(item));
    // data = [{name: "a"}, {name: "b"}]
};
```

## Supported Types

### Simple Values (unchanged behavior)
- Strings: passed as-is
- Numbers: converted to string representation
- Booleans: converted to "true" or "false"

### Complex Values (new JSON serialization)
- Objects (Map): serialized as JSON objects `{"key":"value"}`
- Arrays (List): serialized as JSON arrays `["value1","value2"]`
- Collections (Set, etc.): serialized as JSON arrays
- Nested combinations: fully supported

## Examples

### Example 1: Nested Objects
```javascript
portal.serviceUrl({
    service: 'myservice',
    params: {
        user: {
            name: 'John Doe',
            age: 30,
            active: true
        }
    }
});
// URL: ...?user={"name":"John Doe","age":30,"active":true}
```

### Example 2: Array of Objects
```javascript
portal.serviceUrl({
    service: 'myservice',
    params: {
        items: [
            {id: 1, name: 'Item 1'},
            {id: 2, name: 'Item 2'}
        ]
    }
});
// URL: ...?items={"id":1,"name":"Item 1"}&items={"id":2,"name":"Item 2"}
```

### Example 3: Mixed Parameters
```javascript
portal.serviceUrl({
    service: 'myservice',
    params: {
        simple: 'text',
        number: 42,
        nested: {key: 'value'}
    }
});
// URL: ...?simple=text&number=42&nested={"key":"value"}
```

## Parsing in Services

To parse JSON parameters in your service:

```javascript
exports.get = function(req) {
    // For single nested objects
    const user = JSON.parse(req.params.user);
    
    // For arrays of nested objects
    const items = req.params.items.map(item => JSON.parse(item));
    
    // Or use a helper function
    function parseParam(param) {
        if (!param) return param;
        try {
            return JSON.parse(param);
        } catch (e) {
            return param; // Return as-is if not valid JSON
        }
    }
    
    const processedParams = {
        simple: req.params.simple,  // Already a string
        number: parseInt(req.params.number),
        nested: parseParam(req.params.nested)
    };
};
```

## Backward Compatibility

This change is backward compatible:
- Simple values (strings, numbers, booleans) work exactly as before
- Only nested objects and arrays behavior has changed
- The old workaround of manually using `JSON.stringify()` still works but is no longer necessary

## Character Escaping

All JSON special characters are properly escaped:
- Quotes: `"` → `\"`
- Backslash: `\` → `\\`
- Newline: `\n` → `\\n`
- Tab: `\t` → `\\t`
- Carriage return: `\r` → `\\r`
- Form feed: `\f` → `\\f`
- Backspace: `\b` → `\\b`
