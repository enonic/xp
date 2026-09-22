var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

exports.createInvalidMacro = function () {
    assert.assertThrows(() => schemaLib.createMacro({
        key: null,
        resource: 'kind: "Macro"'
    }));
};
