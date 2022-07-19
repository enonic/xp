var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.updateInvalidContentSchema = function () {
    assert.assertThrows(() => schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'CONTENT_TYPE',
        resource: `<?xml version="1.0" encoding="UTF-8"?>
        <content-type xmlns="urn:enonic:xp:model:1.0">
        </content-type>`
    }));
};

exports.updateInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.updateSchema({
        name: 'myapp:mydata',
        type: 'INVALID_TYPE',
        resource: ''
    }));
};


