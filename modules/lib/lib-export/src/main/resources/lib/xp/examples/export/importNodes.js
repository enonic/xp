/* global resolve*/
var exportLib = require('/lib/xp/export');
var t = require('/lib/xp/testing');

// BEGIN
// Import from application resource files
let importNodes = exportLib.importNodes({
    source: resolve('/import'),
    targetNodePath: '/content',
    xslt: 'transform.xslt',
    xsltParams: {'k': 'v'},
    includeNodeIds: true,
    includePermissions: true
});
// END
// BEGIN
// Information about imported nodes.
var expected = {
    'addedNodes': [
        '/added'
    ],
    'updatedNodes': [
        '/updated'
    ],
    'skippedNodes': [],
    'importedBinaries': [
        'binaryPath [ref]'
    ],
    'importErrors': [
        {
            'exception': 'com.enonic.xp.lib.export.ImportHandlerTest$NoStacktraceException',
            'message': 'error',
            'stacktrace': []
        }
    ]
};
// END

t.assertJsonEquals(expected, importNodes);
