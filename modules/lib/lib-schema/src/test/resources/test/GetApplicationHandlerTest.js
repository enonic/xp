var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.getMissing = function () {
    var result = schemaLib.get({
        key: 'missing',
    });

    assert.assertNull(result);
}
