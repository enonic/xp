var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

exports.getMissing = function () {
    var result = appLib.get({
        key: 'missing',
    });

    assert.assertNull(result);
}
