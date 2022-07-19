var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.getSchema({
        key: 'myapp:mydata',
        type: 'INVALID_TYPE'
    }));
};


