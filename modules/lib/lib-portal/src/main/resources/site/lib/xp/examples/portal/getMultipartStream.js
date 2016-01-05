var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var stream = portalLib.getMultipartStream('item1');
// END

assert.assertNotNull(stream);
