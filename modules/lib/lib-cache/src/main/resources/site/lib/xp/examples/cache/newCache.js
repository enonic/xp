var cacheLib = require('/lib/xp/cache');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a new cache.
var cache = cacheLib.newCache({
    size: 100, // max number of elements
    expire: 60 // added elements expire in 1 minute
});
// END

assert.assertEquals(0, cache.getSize());

// BEGIN
// Gets (or calcuates) a cache value.
var value = cache.get('mykey', function () {
    return 'myvalue';
});
// END

// BEGIN
// Returns the number of elements in cache.
var size = cache.getSize();
// END

assert.assertEquals(1, size);

// BEGIN
// Clears the cache.
cache.clear();
// END

assert.assertEquals(0, cache.getSize());
