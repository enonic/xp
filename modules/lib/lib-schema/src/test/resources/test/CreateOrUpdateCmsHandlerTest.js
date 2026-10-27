var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.createInvalidCms = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateCms({
        application: 'myapp',
        resource: `unsupportedField: "value"`
    }));
};

exports.createSystemCms = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateCms({
        application: 'base',
        resource: `kind: "CMS"`
    }));
};
