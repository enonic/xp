var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.getSchema({
        name: 'myapp:mydata',
        type: 'INVALID_TYPE'
    }));
};

exports.getNullSchema = function () {
    assert.assertNull(schemaLib.getSchema({
        name: 'myapp:non-existing-schema',
        type: 'MIXIN'
    }));
};


