var assert = require('/lib/xp/assert.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "creator": "user:system:anonymous",
    "createdTime": "1975-01-08T00:00:00Z",
    "type": "test:myContentType",
    "displayName": "My Content",
    "hasChildren": false,
    "language": "es",
    "valid": false,
    "data": {
        "a": 1,
        "b": 2,
        "c": [
            "1",
            "2"
        ],
        "d": {
            "e": {
                "f": 3.6,
                "g": true
            }
        }
    },
    "x": {
        "com-enonic-myapplication": {
            "myschema": {
                "a": 1
            }
        }
    },
    "page": {}
};

exports.createContent = function () {
    var result = content.create({
        name: 'mycontent',
        parentPath: '/a/b',
        displayName: 'My Content',
        draft: true,
        contentType: 'test:myContentType',
        language: 'es',
        data: {
            a: 1,
            b: 2,
            c: ['1', '2'],
            d: {
                e: {
                    f: 3.6,
                    g: true
                }
            }
        },
        x: {
            "com-enonic-myapplication": {
                myschema: {
                    a: 1
                }
            }
        }
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.createContentNameAlreadyExists = function () {
    try {
        content.create({
            name: 'mycontent',
            parentPath: '/a/b',
            displayName: 'My Content',
            contentType: 'test:myContentType',
            data: {}
        });

    } catch (e) {
        assert.assertEquals("contentAlreadyExists", e.code);
        return;
    }

    throw {message: "Expected exception"};
};

var expectedJsonAutoGenerateName = {
    "_id": "123456",
    "_name": "my-content",
    "_path": "/a/b/my-content",
    "creator": "user:system:anonymous",
    "createdTime": "1975-01-08T00:00:00Z",
    "type": "test:myContentType",
    "displayName": "My Content",
    "hasChildren": false,
    "valid": false,
    "data": {},
    "x": {},
    "page": {}
};

exports.createContentAutoGenerateName = function () {
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        data: {}
    });

    assert.assertJsonEquals(expectedJsonAutoGenerateName, result);
};

var expectedJsonAutoGenerateName2 = {
    "_id": "123456",
    "_name": "my-content-3",
    "_path": "/a/b/my-content-3",
    "creator": "user:system:anonymous",
    "createdTime": "1975-01-08T00:00:00Z",
    "type": "test:myContentType",
    "displayName": "My Content",
    "hasChildren": false,
    "valid": false,
    "data": {},
    "x": {},
    "page": {}
};

exports.createContentAutoGenerateNameWithExistingName = function () {
    var counter = 1;
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        data: {},
        idGenerator: function () {
            return String(counter++);
        }
    });

    assert.assertJsonEquals(expectedJsonAutoGenerateName2, result);
};

