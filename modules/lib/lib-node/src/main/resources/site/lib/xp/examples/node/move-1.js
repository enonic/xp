var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Rename content by id. Keeps same parent.
var content1 = nodeLib.move({
    source: 'nodeId',
    target: 'new-name'
});

log.info('New path: ' + content1._path); // '/new-name'
// END

assert.assertEquals('/new-name', content1._path);
