var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "com.enonic.cms.default",
    branch: "master"
});

var result = repo.setActiveVersion({
    key: 'nodeId',
    versionId: '90398ddd1'
});

assert.assertTrue(result);
