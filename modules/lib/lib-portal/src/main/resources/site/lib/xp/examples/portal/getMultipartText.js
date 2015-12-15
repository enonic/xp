var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var text = portalLib.getMultipartText('item1');
// END

assert.assertNotNull(text);
