var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getComponentNotFound = function () {
    assert.assertNull(schemaLib.getPart({
        key: 'myapp:mydata'
    }));
};
