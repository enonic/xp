var exportLib = require('/lib/xp/export');
var t = require('/lib/xp/testing');

// BEGIN
// Import from exports directory without XSLT transformation.
let importNodes = exportLib.importNodes({
    source: 'myexport',
    targetNodePath: '/content'
});
// END
// BEGIN
// Information about imported nodes.
var expected = {
    'addedNodes': [
        '/added'
    ],
    'updatedNodes': [],
    'skippedNodes': [],
    'importedBinaries': [],
    'importErrors': []
};
// END

t.assertJsonEquals(expected, importNodes);
