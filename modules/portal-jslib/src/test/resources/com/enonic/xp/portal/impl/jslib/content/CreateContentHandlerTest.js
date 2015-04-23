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
    "page": {},
    "type": "test:myContentType",
    "valid": false,
    "x": {
        "com-enonic-mymodule": {
            "myschema": {
                "a": 1.0
            }
        }
    }
};

exports.createContent = function () {
    var result = execute('content.create', {
        name: 'mycontent',
        parentPath: '/a/b',
        displayName: 'My Content',
        draft: true,
        contentType: 'test:myContentType',
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
            "com-enonic-mymodule": {
                myschema: {
                    a: 1
                }
            }
        }
    });

    assert.assertJson(expectedJson, result);
};
