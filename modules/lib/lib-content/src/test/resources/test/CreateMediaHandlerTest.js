var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var TestClass = Java.type('com.enonic.xp.lib.content.CreateMediaHandlerTest');

var expectedJson = {
    '_id': '123456',
    '_name': 'mycontent',
    '_path': '/a/b/mycontent',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'base:unstructured',
    'valid': false,
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createMedia = function () {
    var result = content.createMedia({
        name: 'mycontent',
        parentPath: '/a/b',
        mimeType: 'text/plain',
        data: TestClass.createByteSource('Hello World')
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.createMediaWithFocalPoints = function () {
    var result = content.createMedia({
        name: 'mycontent',
        parentPath: '/a/b',
        mimeType: 'text/plain',
        focalY: 0.1,
        focalX: 0.3,
        data: TestClass.createByteSource('Hello World')
    });

    assert.assertJsonEquals(expectedJson, result);
};

var expectedJsonAutoGenerateName = {
    '_id': '123456',
    '_name': 'my-content-3.jpg',
    '_path': '/a/b/my-content-3.jpg',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'base:unstructured',
    'valid': false,
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createMediaAutoGenerateName = function () {
    var counter = 1;
    var result = content.createMedia({
        name: 'my-content.jpg',
        parentPath: '/a/b',
        mimeType: 'text/plain',
        data: TestClass.createByteSource('Hello World'),
        idGenerator() {
            return String(counter++);
        }
    });

    assert.assertJsonEquals(expectedJsonAutoGenerateName, result);
};

var expectedDocumentJson = {
    '_id': 'dbc077af-fb97-4b17-a567-ad69e85f1010',
    '_name': 'documentName.pdf',
    '_path': '/a/b/documentName.pdf',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'media:document',
    'displayName': 'documentName.pdf',
    'valid': true,
    'data': {
        'media': {
            'attachment': 'documentName.pdf'
        }
    },
    'x': {},
    'page': {},
    'attachments': {
        'documentName.pdf': {
            'name': 'documentName.pdf',
            'label': 'source',
            'size': 653453,
            'mimeType': 'application/pdf'
        }
    },
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createMediaAsPDF = function () {
    var result = content.createMedia({
        name: 'documentName.pdf',
        parentPath: '/a/b',
        mimeType: 'application/pdf',
        data: TestClass.createByteSource('Some text')
    });

    assert.assertJsonEquals(expectedDocumentJson, result);
};
