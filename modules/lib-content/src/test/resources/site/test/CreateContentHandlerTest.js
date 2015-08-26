var assert = Java.type('org.junit.Assert');
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

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

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

    assertJson(expectedJson, result);
};
