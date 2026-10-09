var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidCms = function () {
    assert.assertThrows(() => schemaLib.updateCms({
        application: 'myapp',
        resource: `unsupportedField: "value"`
    }));
};
