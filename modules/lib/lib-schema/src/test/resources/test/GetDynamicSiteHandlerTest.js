var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.getNull = function () {
    assert.assertThrows(() => schemaLib.getSite({
        application: null
    }));
};


