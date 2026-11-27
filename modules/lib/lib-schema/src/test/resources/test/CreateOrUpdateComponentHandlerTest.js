var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global testInstance */

exports.createOrUpdatePartKeepingIcon = function () {
    var result = schemaLib.createOrUpdatePart({
        key: 'myapp:mypart',
        resource: 'kind: "Part"'
    });
    assert.assertEquals('myapp:mypart', result.key);
};

exports.createOrUpdatePartRemovingIcon = function () {
    var result = schemaLib.createOrUpdatePart({
        key: 'myapp:mypart',
        icon: null
    });
    assert.assertEquals('myapp:mypart', result.key);
};

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
    assert.assertThrows(() => schemaLib.createOrUpdatePart({
        resource: 'kind: "Part"'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdateLayout({
        resource: 'kind: "Layout"'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdatePage({
        resource: 'kind: "Page"'
    }));
};

// layouts and pages have no icon, so the resource is the only thing to store and is required up front
exports.createComponentWithoutResource = function () {
    assert.assertThrows(() => schemaLib.createOrUpdateLayout({
        key: 'myapp:mylayout'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdatePage({
        key: 'myapp:mypage'
    }));
};

exports.createComponentWithInvalidKey = function () {
    assert.assertThrows(() => schemaLib.createOrUpdatePart({
        key: 'mypart',
        resource: 'kind: "Part"'
    }));
    assert.assertThrows(() => schemaLib.createOrUpdatePart({
        key: 'myapp:my part',
        resource: 'kind: "Part"'
    }));
};

exports.createPartWithIncompleteIcon = function () {
    assert.assertThrows(() => schemaLib.createOrUpdatePart({
        key: 'myapp:mypart',
        resource: 'kind: "Part"',
        icon: {
            data: testInstance.createByteSource('<svg/>')
        }
    }));
    assert.assertThrows(() => schemaLib.createOrUpdatePart({
        key: 'myapp:mypart',
        resource: 'kind: "Part"',
        icon: {
            mimeType: 'image/svg+xml'
        }
    }));
};