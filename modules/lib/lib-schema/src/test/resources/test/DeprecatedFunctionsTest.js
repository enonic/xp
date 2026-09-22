var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

// The generic functions are deprecated wrappers over the per-type ones: each dispatches on `type`.

exports.createSchema = function () {
    var result = schemaLib.createSchema({
        name: 'myapp:mytype',
        type: 'CONTENT_TYPE',
        resource: 'kind: "ContentType"'
    });
    assert.assertEquals('myapp:mytype', result.name);
    assert.assertEquals('CONTENT_TYPE', result.type);
};

exports.updateSchema = function () {
    var result = schemaLib.updateSchema({
        name: 'myapp:myfragment',
        type: 'FORM_FRAGMENT',
        resource: 'kind: "FormFragment"'
    });
    assert.assertEquals('myapp:myfragment', result.name);
    assert.assertEquals('FORM_FRAGMENT', result.type);
};

exports.getSchema = function () {
    var result = schemaLib.getSchema({
        name: 'myapp:mymixin',
        type: 'MIXIN'
    });
    assert.assertEquals('myapp:mymixin', result.name);
    assert.assertEquals('MIXIN', result.type);
};

exports.listSchemas = function () {
    var result = schemaLib.listSchemas({
        application: 'myapp',
        type: 'CONTENT_TYPE'
    });
    assert.assertEquals(1, result.length);
    assert.assertEquals('myapp:mytype', result[0].name);
};

exports.deleteSchema = function () {
    assert.assertTrue(schemaLib.deleteSchema({
        name: 'myapp:mytype',
        type: 'CONTENT_TYPE'
    }));
};

exports.unsupportedSchemaType = function () {
    assert.assertThrows(() => schemaLib.createSchema({
        name: 'myapp:mytype',
        type: 'PART',
        resource: 'kind: "Part"'
    }));
    assert.assertThrows(() => schemaLib.getSchema({
        name: 'myapp:mytype',
        type: 'PART'
    }));
};

exports.createComponent = function () {
    var result = schemaLib.createComponent({
        key: 'myapp:mypart',
        type: 'PART',
        resource: 'kind: "Part"'
    });
    assert.assertEquals('myapp:mypart', result.key);
    assert.assertEquals('PART', result.type);
};

exports.updateComponent = function () {
    var result = schemaLib.updateComponent({
        key: 'myapp:mypart',
        type: 'PART',
        resource: 'kind: "Part"'
    });
    assert.assertEquals('myapp:mypart', result.key);
    assert.assertEquals('PART', result.type);
};

exports.getComponent = function () {
    var result = schemaLib.getComponent({
        key: 'myapp:mypart',
        type: 'PART'
    });
    assert.assertEquals('myapp:mypart', result.key);
    assert.assertEquals('PART', result.type);
};

exports.listComponents = function () {
    var result = schemaLib.listComponents({
        application: 'myapp',
        type: 'PART'
    });
    assert.assertEquals(1, result.length);
    assert.assertEquals('myapp:mypart', result[0].key);
};

exports.deleteComponent = function () {
    assert.assertTrue(schemaLib.deleteComponent({
        key: 'myapp:mypart',
        type: 'PART'
    }));
};

exports.unsupportedComponentType = function () {
    assert.assertThrows(() => schemaLib.createComponent({
        key: 'myapp:mypart',
        type: 'MIXIN',
        resource: 'kind: "Mixin"'
    }));
    assert.assertThrows(() => schemaLib.listComponents({
        application: 'myapp',
        type: 'MIXIN'
    }));
};

exports.getSite = function () {
    var result = schemaLib.getSite({
        application: 'myapp'
    });
    assert.assertEquals('myapp', result.application);
};

exports.updateSite = function () {
    var result = schemaLib.updateSite({
        application: 'myapp',
        resource: 'kind: "CMS"'
    });
    assert.assertEquals('myapp', result.application);
};
