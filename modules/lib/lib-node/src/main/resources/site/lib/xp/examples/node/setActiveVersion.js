var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

var versionId = repo.setActiveVersion({
    key: 'nodeId',
    versionId: '90398ddd1'
});

assert.assertEquals('90398ddd1', versionId);
