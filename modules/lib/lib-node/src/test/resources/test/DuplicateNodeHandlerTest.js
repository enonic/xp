var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'draft'
});

exports.testInvalidParams = function () {
    try {
        repo.duplicate({});
    } catch (e) {
        assert.assertEquals(`Parameter 'nodeId' is required`, e.message);
    }
};
