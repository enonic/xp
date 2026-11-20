var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance */

exports.createOrUpdateContentTypeIcon = function () {
    var result = schemaLib.createOrUpdateContentType({
        name: 'myapp:mytype',
        icon: {
            data: testInstance.createByteSource('<svg/>'),
            mimeType: 'image/svg+xml'
        }
    });
    assert.assertEquals('myapp:mytype', result.name);
};

exports.createOrUpdateMixinRemovingIcon = function () {
    var result = schemaLib.createOrUpdateMixin({
        name: 'myapp:mymixin',
        icon: null
    });
    assert.assertEquals('myapp:mymixin', result.name);
};

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
    assert.assertThrows(() => schemaLib.createOrUpdateContentType({
        resource: 'kind: "ContentType"'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdateFormFragment({
        resource: 'kind: "FormFragment"'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdateMixin({
        resource: 'kind: "Mixin"'
    }));
};

// `name` must be `<application>:<localName>`: the handler fails to parse the schema name.
exports.createContentSchemaWithInvalidName = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateContentType({
        name: 'mydata',
        resource: 'kind: "ContentType"'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdateContentType({
        name: 'myapp:my data',
        resource: 'kind: "ContentType"'
    }));
};