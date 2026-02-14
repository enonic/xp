var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
// Simple parameters
var url1 = portalLib.serviceUrl({
    service: 'myservice',
    params: {
        a: 1,
        b: 2
    }
});

// Nested object parameters - will be JSON-serialized
var url2 = portalLib.serviceUrl({
    service: 'myservice',
    params: {
        data: {
            name: 'John',
            age: 30
        },
        items: [
            {id: 1, name: 'Item 1'},
            {id: 2, name: 'Item 2'}
        ]
    }
});
// END

assert.assertEquals('ServiceUrlParams{type=server, params={a=[1], b=[2]}, service=myservice}', url1);
assert.assertEquals('ServiceUrlParams{type=server, params={data=[{"name":"John","age":30}], items=[{"id":1,"name":"Item 1"}, {"id":2,"name":"Item 2"}]}, service=myservice}', url2);
