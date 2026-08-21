var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Delete dynamic mixin icon.
var result = schemaLib.deleteMixinIcon({
    name: 'myapp:mymixin'
});

// END


assert.assertTrue(result);
