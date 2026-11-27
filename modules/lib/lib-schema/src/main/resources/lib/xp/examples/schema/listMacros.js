var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic macros of an application.
var result = schemaLib.listMacros({
    application: 'myapp'
});

log.info('Fetched macros: ' + result.length);

// END


assert.assertEquals(1, result.length);
assert.assertEquals('myapp:mymacro', result[0].key);
assert.assertEquals('mymacro', result[0].name);
assert.assertEquals('My Macro', result[0].title);
assert.assertEquals('image/svg+xml', result[0].icon.mimeType);
