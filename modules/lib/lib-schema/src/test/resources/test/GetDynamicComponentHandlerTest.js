var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getInvalidComponentType = function () {
    assert.assertThrows(() => schemaLib.getComponent({
        key: 'myapp:mydata',
        type: 'INVALID_TYPE'
    }));
};

exports.getComponentNotFound = function () {
    assert.assertNull(schemaLib.getComponent({
        key: 'myapp:mydata',
        type: 'PART'
    }));
};


