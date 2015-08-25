var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "createdTime": "1975-01-08T00:00:00Z",
    "creator": "user:system:anonymous",
    "data": {
        "a": 1,
        "b": 2,
        "c": ["1", "2"],
        "d": {
            "e": {
                "f": 3.6,
                "g": true
            }
        }
    },
    "displayName": "My Content",
    "hasChildren": false,
    "language": "es",
    "page": {},
    "type": "test:myContentType",
    "valid": false,
    "x": {
        "com-enonic-myapplication": {
            "myschema": {
                "a": 1.0
            }
        }
    }
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

    scriptAssert.assertJson(expectedJson, result);
};
