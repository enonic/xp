var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var text = portalLib.getMultipartText('item1');
// END

assert.assertNotNull(text);
