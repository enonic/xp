var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// BEGIN
// Delete dynamic content type icon.
var result = schemaLib.deleteContentTypeIcon({
    name: 'myapp:mytype'
});

// END


assert.assertTrue(result);
