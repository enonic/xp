var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
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
    "draft": true,
    "hasChildren": false,
    "meta": {
        "test": {
            "a": 1
        }
    },
    "page": {},
    "type": "base:unstructured"
};

exports.createContent = function () {
    var result = execute('content.create', {
        name: 'mycontent',
        parentPath: '/a/b',
        displayName: 'My Content',
        draft: true,
        contentType: 'base:unstructured',
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
        meta: {
            test: {
                a: 1
            }
        }
    });

    assert.assertJson(expectedJson, result);
};
