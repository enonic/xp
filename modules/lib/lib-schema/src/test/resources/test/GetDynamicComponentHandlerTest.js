var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getInvalidComponentType = function () {
    assert.assertThrows(() => schemaLib.getComponent({
        key: 'myapp:mydata',
        type: 'INVALID_TYPE'
    }));
};


