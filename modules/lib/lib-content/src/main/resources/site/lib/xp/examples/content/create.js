var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a content.
var result1 = contentLib.create({
    name: 'mycontent',
    parentPath: '/a/b',
    displayName: 'My Content',
    branch: 'draft',
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
    },
    "attachments": {}
});

log.info('Content created with id ' + result1._id);
// END

// BEGIN
// Check if content already exists.
try {
    var result2 = contentLib.create({
        name: 'mycontent',
        parentPath: '/a/b',
        displayName: 'My Content',
        branch: 'draft',
        contentType: 'test:myContentType',
        data: {}
    });

    log.info('Content created with id ' + result2._id);

} catch (e) {
    if (e.code == 'contentAlreadyExists') {
        log.error('There is already a content with that name');
    } else {
        log.error('Unexpected error: ' + e.message);
    }
}
// END

// BEGIN
// Content created.
var expected = {
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
    "page": {},
    "attachments": {}
};
// END

assert.assertJsonEquals(expected, result1);

assert.assertJsonEquals({
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "creator": "user:system:anonymous",
    "createdTime": "1975-01-08T00:00:00Z",
    "type": "test:myContentType",
    "displayName": "My Content",
    "hasChildren": false,
    "valid": false,
    "data": {},
    "x": {},
    "page": {},
    "attachments": {}
}, result2);
