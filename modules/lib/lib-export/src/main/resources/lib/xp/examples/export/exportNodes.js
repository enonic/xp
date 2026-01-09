/* global*/
var exportLib = require('/lib/xp/export');
var t = require('/lib/xp/testing');

// BEGIN
// Export content nodes.
let exportNodes = exportLib.exportNodes({
    sourceNodePath: '/content',
    exportName: 'export-1',
    includeNodeIds: true
});
// END

// BEGIN
// Information about exported nodes.
var expected = {
    'exportedNodes': [
        '/content'
    ],
    'exportedBinaries': [
        '/binaryPath [ref]'
    ],
    'exportErrors': [
        'some error'
    ]
};
// END

t.assertJsonEquals(expected, exportNodes);
