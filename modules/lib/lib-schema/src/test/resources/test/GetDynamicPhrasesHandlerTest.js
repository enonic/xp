var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.getNotFound = function () {
    assert.assertNull(schemaLib.getPhrases({
        application: 'myapp',
        name: 'missing'
    }));
};
