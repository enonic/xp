var exportLib = require('/lib/xp/export');
var t = require('/lib/xp/testing');

// BEGIN
// List all available node-exports.
var listResult = exportLib.list();
// END
// BEGIN
// Information about available exports.
var expected = {
    'exports': [
        {'name': 'export-a'},
        {'name': 'export-b'}
    ]
};
// END

t.assertJsonEquals(expected, listResult);
