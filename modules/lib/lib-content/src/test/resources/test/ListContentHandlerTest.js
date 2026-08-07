var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.listByIdRecursive = function () {

    var result = content.list({
        parent: 'content-id',
        recursive: true
    });

    assert.assertEquals(0, result.count);
    assert.assertEquals(0, result.hits.length);
};
