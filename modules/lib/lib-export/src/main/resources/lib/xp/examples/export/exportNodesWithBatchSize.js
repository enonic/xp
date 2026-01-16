/* global*/
var exportLib = require('/lib/xp/export');
var t = require('/lib/xp/testing');

// BEGIN
// Export content nodes with custom batch size.
let exportNodes = exportLib.exportNodes({
    sourceNodePath: '/content',
    exportName: 'export-batch',
    batchSize: 25
});
// END

// BEGIN
// Information about exported nodes.
var expected = {
    'exportedNodes': [
        '/content'
    ],
    'exportedBinaries': [],
    'exportErrors': []
};
// END

t.assertJsonEquals(expected, exportNodes);

