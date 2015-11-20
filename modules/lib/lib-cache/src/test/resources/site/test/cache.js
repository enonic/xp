var assert = require('/lib/xp/assert.js');
var cacheLib = require('/lib/xp/cache.js');

var cache = cacheLib.newCache({
    size: 100,
    expire: 10
});

assert.assertEquals(0, cache.size());

var numCalled = 0;
var calcFunction = function () {
    numCalled++;

    return {
        num: numCalled,
        name: 'value' + numCalled
    };
};

var result = cache.get('key1', calcFunction);
assert.assertEquals(1, result.num);
assert.assertEquals('value1', result.name);
assert.assertEquals(1, cache.size());

result = cache.get('key1', calcFunction);
assert.assertEquals(1, result.num);
assert.assertEquals('value1', result.name);
assert.assertEquals(1, cache.size());

result = cache.get('key2', calcFunction);
assert.assertEquals(2, result.num);
assert.assertEquals('value2', result.name);
assert.assertEquals(2, cache.size());

cache.clear();
assert.assertEquals(0, cache.size());
