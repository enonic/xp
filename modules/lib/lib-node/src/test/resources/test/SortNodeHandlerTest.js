var assert = require('/lib/xp/testing.js');
var nodeLib = require('/lib/xp/node.js');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

exports.reorderWithoutChildOrder = function () {

    var result = repo.sort({
        key: 'nodeid',
        reorder: [
            {nodeId: 'child-1', afterOrderKey: '3a00000zzzz.above', beforeOrderKey: '3a00001zzzz.below'},
            {nodeId: 'child-2'}
        ]
    });

    assert.assertEquals('nodeid', result.node._id);
};

exports.missingParams = function () {

    var threw = false;
    try {
        repo.sort({key: 'nodeid'});
    } catch (e) {
        threw = true;
    }

    assert.assertEquals(true, threw);
};
