var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

var node = repo.setChildOrder({
    key: 'nodeId',
    childOrder: 'field DESC'
});

assert.assertNotNull(node);
