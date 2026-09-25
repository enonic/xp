var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

exports.createInvalidComponent = function () {
    assert.assertThrows(() => schemaLib.createComponent({
        key: 'myapp:mydata',
        type: 'PART',
        resource: `<?xml version="1.0" encoding="UTF-8"?>
        <part xmlns="urn:enonic:xp:model:1.0">
                    <wrong-tag></wrong-tag>
        </part>`
    }));
};

exports.createInvalidComponentType = function () {
    assert.assertThrows(() => schemaLib.createComponent({
        key: 'myapp:mydata',
        type: 'INVALID_TYPE',
        resource: ''
    }));
};

exports.createComponentWithoutKey = function () {
    assert.assertThrows(() => schemaLib.createPart({
        resource: 'kind: "Part"'
    }));
    assert.assertThrows(() => schemaLib.createLayout({
        resource: 'kind: "Layout"'
    }));
    assert.assertThrows(() => schemaLib.createPage({
        resource: 'kind: "Page"'
    }));
};

exports.createComponentWithInvalidKey = function () {
    assert.assertThrows(() => schemaLib.createPart({
        key: 'mypart',
        resource: 'kind: "Part"'
    }));
    assert.assertThrows(() => schemaLib.createPart({
        key: 'myapp:my part',
        resource: 'kind: "Part"'
    }));
};


