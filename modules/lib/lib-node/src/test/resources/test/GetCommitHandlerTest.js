var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'cms-repo',
    branch: 'draft'
});

exports.testEmpty = function () {
    var result = repo.getCommit({
        id: '123'
    });

    assert.assertNull(result);
};
