var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.createInvalidContentSchema = function () {
    assert.assertThrows(() => schemaLib.createSchema({
        name: 'myapp:mydata',
        type: 'CONTENT_TYPE',
        resource: `<?xml version="1.0" encoding="UTF-8"?>
        <content-type xmlns="urn:enonic:xp:model:1.0">
        </content-type>`
    }));
};

exports.createInvalidContentSchemaType = function () {
    assert.assertThrows(() => schemaLib.createSchema({
        name: 'myapp:mydata',
        type: 'INVALID_TYPE',
        resource: ''
    }));
};

// `name` is required: the TS layer rejects the call before the handler is reached.
exports.createContentSchemaWithoutName = function () {
    assert.assertThrows(() => schemaLib.createContentType({
        resource: 'kind: "ContentType"'
    }));
    assert.assertThrows(() => schemaLib.createFormFragment({
        resource: 'kind: "FormFragment"'
    }));
    assert.assertThrows(() => schemaLib.createMixin({
        resource: 'kind: "Mixin"'
    }));
};

// `name` must be `<application>:<localName>`: the handler fails to parse the schema name.
exports.createContentSchemaWithInvalidName = function () {
    assert.assertThrows(() => schemaLib.createContentType({
        name: 'mydata',
        resource: 'kind: "ContentType"'
    }));
    assert.assertThrows(() => schemaLib.createContentType({
        name: 'myapp:my data',
        resource: 'kind: "ContentType"'
    }));
};


