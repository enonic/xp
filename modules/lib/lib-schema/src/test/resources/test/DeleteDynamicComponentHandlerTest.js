var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.deleteInvalidComponentType = function () {
    assert.assertThrows(() => schemaLib.deleteComponent({
        key: 'myapp:mydata',
        type: 'INVALID_TYPE'
    }));
};


