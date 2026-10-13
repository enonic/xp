var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.createInvalidCms = function () {
    assert.assertThrows(() => schemaLib.createCms({
        application: 'myapp',
        resource: `unsupportedField: "value"`
    }));
};
