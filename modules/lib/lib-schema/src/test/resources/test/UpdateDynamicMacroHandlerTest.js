var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidMacro = function () {
    assert.assertThrows(() => schemaLib.updateMacro({
        key: null,
        resource: `kind: "Macro"`
    }));
};
