var assert = require('/lib/xp/assert.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "creator": "user:system:admin",
    "modifier": "user:system:admin",
    "createdTime": "1970-01-01T00:00:00Z",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "type": "test:myContentType",
    "displayName": "Modified",
    "hasChildren": false,
    "language": "es",
    "valid": false,
    "data": {
        "a": 2,
        "b": "2",
        "c": [
            {
                "d": true
            },
            {
                "d": false,
                "e": [
                    "3",
                    "42",
                    "5"
                ],
                "f": 2
            }
        ],
        "z": "99"
    },
    "x": {
        "com-enonic-myapplication": {
            "myschema": {
                "a": 1
            },
            "other": {
                "name": "test"
            }
        }
    },
    "page": {
        "controller": "myapplication:mycontroller",
        "config": {
            "a": "1"
        },
        "regions": {
            "top": {
                "components": [
                    {
                        "name": "mypart",
                        "path": "top/0",
                        "type": "part",
                        "descriptor": "myapplication:mypart",
                        "config": {
                            "a": "1"
                        }
                    },
                    {
                        "name": "mylayout",
                        "path": "top/1",
                        "type": "layout",
                        "descriptor": "myapplication:mylayout",
                        "config": {
                            "a": "1"
                        },
                        "regions": {
                            "bottom": {
                                "components": [
                                    {
                                        "name": "mypart",
                                        "path": "top/1/bottom/0",
                                        "type": "part",
                                        "descriptor": "myapplication:mypart",
                                        "config": {
                                            "a": "1"
                                        }
                                    }
                                ],
                                "name": "bottom"
                            }
                        }
                    }
                ],
                "name": "top"
            }
        }
    }
};

function editor(c) {
    c.displayName = 'Modified';
    c.data.a++;
    c.data.z = '99';
    c.data.c[1].d = false;
    c.data.c[1].e[1] = 42;

    if (!c.x['com-enonic-myapplication']) {
        c.x['com-enonic-myapplication'] = {};
    }

    c.x['com-enonic-myapplication'].other = {
        name: 'test'
    };

    c.language = 'es';

    return c;
}

exports.modify_notFound = function () {
    var result = content.modify({
        key: '123456',
        editor: editor
    });

    assert.assertNull(result);
};

exports.modifyById = function () {
    var result = content.modify({
        key: '123456',
        editor: editor
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.modifyByPath = function () {
    var result = content.modify({
        key: '/a/b/mycontent',
        editor: editor
    });

    assert.assertJsonEquals(expectedJson, result);
};
