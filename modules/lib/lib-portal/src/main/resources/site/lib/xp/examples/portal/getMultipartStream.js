var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var stream = portalLib.getMultipartStream('item2');
var stream2 = portalLib.getMultipartStream('item2', 1);
// END

assert.assertNotNull(stream);
assert.assertNotNull(stream2);
