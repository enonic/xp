var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.deleteNull = function () {
    assert.assertThrows(() => schemaLib.deleteCms({
        application: null
    }));
};
