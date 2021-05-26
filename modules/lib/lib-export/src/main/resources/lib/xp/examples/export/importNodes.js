var exportLib = require('/lib/xp/export');
var t = require('/lib/xp/testing');

// BEGIN
// END
let importNodes = exportLib.importNodes({
    source: '/import',
    targetNodePath: '/content',
    includeNodeIds: true,
    includePermissions: true
});

// BEGIN
// Information about imported nodes.
var expected = {
    'addedNodes': [],
    'updatedNodes': [],
    'exportedBinaries': [],
    'importErrors': []
};
// END

t.assertJsonEquals(expected, importNodes);
