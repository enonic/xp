var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidSite = function () {
    assert.assertThrows(() => schemaLib.updateSite({
        application: 'myapp',
        resource: `unsupportedField: "value"`
    }));
};
