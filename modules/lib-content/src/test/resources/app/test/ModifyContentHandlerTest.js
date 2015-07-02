var assert = Java.type('org.junit.Assert');
var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "createdTime": "1970-01-01T00:00:00Z",
    "creator": "user:system:admin",
    "data": {
        "a": 2.0,
        "b": "2",
        "c": [{
            "d": true
        }, {
            "d": true,
            "e": ["3", "4", "5"],
            "f": 2
        }],
        "z": "99"
    },
    "displayName": "Modified",
    "hasChildren": false,
    "modifiedTime": "1970-01-01T00:00:00Z",
    "modifier": "user:system:admin",
    "page": {
        "config": {
            "a": "1"
        },
        "controller": "mymodule:mycontroller",
        "regions": {
            "top": {
                "components": [{
                    "config": {
                        "a": "1"
                    },
                    "descriptor": "mymodule:mypart",
                    "name": "mypart",
                    "path": "top/0",
                    "type": "part"
                }, {
                    "config": {
                        "a": "1"
                    },
                    "descriptor": "mymodule:mylayout",
                    "name": "mylayout",
                    "path": "top/1",
                    "regions": {
                        "bottom": {
                            "components": [{
                                "config": {
                                    "a": "1"
                                },
                                "descriptor": "mymodule:mypart",
                                "name": "mypart",
                                "path": "top/1/bottom/0",
                                "type": "part"
                            }],
                            "name": "bottom"
                        }
                    },
                    "type": "layout"
                }],
                "name": "top"
            }
        }
    },
    "type": "test:myContentType",
    "valid": false,
    "x": {
        "com-enonic-mymodule": {
            "myschema": {
                "a": 1.0
            },
            "other": {
                "name": "test"
            }
        }
    }
};

function editor(c) {
    c.displayName = 'Modified';
    c.data.a++;
    c.data.z = '99';

    if (!c.x['com-enonic-mymodule']) {
        c.x['com-enonic-mymodule'] = {};
    }

    c.x['com-enonic-mymodule'].other = {
        name: 'test'
    };

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

    scriptAssert.assertJson(expectedJson, result);
};

exports.modifyByPath = function () {
    var result = content.modify({
        key: '/a/b/mycontent',
        editor: editor
    });

    scriptAssert.assertJson(expectedJson, result);
};
