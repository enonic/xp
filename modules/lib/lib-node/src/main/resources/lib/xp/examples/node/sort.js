var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

var node = repo.sort({
    key: 'nodeId',
    childOrder: 'field DESC'
});

assert.assertNotNull(node);
