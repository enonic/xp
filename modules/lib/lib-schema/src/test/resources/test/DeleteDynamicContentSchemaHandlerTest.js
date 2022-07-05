var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.deleteInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.deleteSchema({
        name: 'myapp:mydata',
        type: 'INVALID_TYPE'
    }));
};


