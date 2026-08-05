var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getNullSchema = function () {
    assert.assertNull(schemaLib.getFormFragment({
        name: 'non.existing:schema'
    }));
};