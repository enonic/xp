var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.updateInvalidMacro = function () {
    assert.assertThrows(() => schemaLib.updateMacro({
        key: 'myapp:mymacro',
        resource: null
    }));
};
