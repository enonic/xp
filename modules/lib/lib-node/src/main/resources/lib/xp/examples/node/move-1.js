var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "com.enonic.cms.default",
    branch: "master"
});

// BEGIN
// Rename content by id. Keeps same parent.
var content1 = repo.move({
    source: 'nodeId',
    target: 'new-name'
});

log.info('New path: ' + content1._path); // '/new-name'
// END

assert.assertEquals('/new-name', content1._path);
