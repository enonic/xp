var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Delete dynamic form fragment icon.
var result = schemaLib.deleteFormFragmentIcon({
    name: 'myapp:myfragment'
});

// END


assert.assertTrue(result);
