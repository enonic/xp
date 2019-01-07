var assert = require('/lib/xp/testing.js');
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
        "type": "page",
        "path": "/",
        "descriptor": "my-app-key:mycontroller",
        "config": {
            "a": "1"
        },
        "regions": {
            "top": {
                "components": [
                    {
                        "path": "/top/0",
                        "type": "part",
                        "descriptor": "app-descriptor-x:name-x",
                        "config": {
                            "a": "1"
                        }
                    },
                    {
                        "path": "/top/1",
                        "type": "layout",
                        "descriptor": "layoutDescriptor:name",
                        "config": {},
                        "regions": {
                            "left": {
                                "components": [
                                    {
                                        "path": "/top/1/left/0",
                                        "type": "part",
                                        "config": {}
                                    },
                                    {
                                        "path": "/top/1/left/1",
                                        "type": "text",
                                        "text": "text text text"
                                    },
                                    {
                                        "path": "/top/1/left/2",
                                        "type": "text",
                                        "text": ""
                                    }
                                ]
                            },
                            "right": {
                                "components": [
                                    {
                                        "path": "/top/1/right/0",
                                        "type": "image"
                                    },
                                    {
                                        "path": "/top/1/right/1",
                                        "type": "fragment",
                                        "fragment": "213sda-ss222"
                                    }
                                ]
                            }
                        }
                    },
                    {
                        "path": "/top/2",
                        "type": "layout",
                        "config": {},
                        "regions": {}
                    }
                ]
            },
            "bottom": {
                "components": [
                    {
                        "path": "/bottom/0",
                        "type": "part",
                        "descriptor": "app-descriptor-y:name-y",
                        "config": {
                            "a": "1"
                        }
                    },
                    {
                        "path": "/bottom/1",
                        "type": "image"
                    },
                    {
                        "path": "/bottom/2",
                        "type": "image"
                    }
                ]
            }
        }
    },
    "attachments": {},
    "publish": {
        "from": "2018-11-03T10:00:01Z",
        "to": "2018-11-03T10:00:01Z",
    },
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

    c.publish.from = '2018-11-03T10:00:01Z';
    c.publish.to = '2018-11-03T10:00:01Z';

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
