var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.getNull = function () {
    assert.assertThrows(() => schemaLib.getMacro({
        key: null
    }));
};

exports.getNotFound = function () {
    assert.assertNull(schemaLib.getMacro({
        key: 'myapp:missing'
    }));
};
