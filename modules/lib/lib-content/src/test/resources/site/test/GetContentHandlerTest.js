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
    "type": "base:unstructured",
    "displayName": "My Content",
    "hasChildren": false,
    "language": "en",
    "valid": false,
    "data": {
        "boolean": [
            true,
            true,
            false
        ],
        "long": 1,
        "longs": [
            1,
            2,
            3
        ],
        "double": 2.2,
        "doubles": [
            1.1,
            2.2,
            3.3
        ],
        "string": "a",
        "strings": [
            "a",
            "b",
            "c"
        ],
        "stringEmpty": "",
        "set": {
            "property": "value"
        },
        "xml": "<xml><my-xml hello='world'/></xml>",
        "binaryReference": "abc",
        "link": "/my/content",
        "geoPoint": "1.1,-1.1",
        "geoPoints": [
            "1.1,-1.1",
            "2.2,-2.2"
        ],
        "instant": "+1000000000-12-31T23:59:59.999999999Z",
        "localDate": "2014-01-31",
        "localDateTime": "2014-01-31T10:30:05",
        "c": {
            "d": true,
            "e": [
                "3",
                "4",
                "5"
            ],
            "f": 2
        }
    },
    "x": {
        "com-enonic-myapplication": {
            "myschema": {
                "a": "1"
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
    "attachments": {
        "logo.png": {
            "name": "logo.png",
            "label": "small",
            "size": 6789,
            "mimeType": "image/png"
        },
        "document.pdf": {
            "name": "document.pdf",
            "size": 12345,
            "mimeType": "application/pdf"
        }
    },
    "publish": {
        "from": "2016-11-03T10:00:00Z",
        "to": "2016-11-23T10:00:00Z"
    }
};

var pageAsFragmentJson = {
    "fragment": {
        "type": "layout",
        "descriptor": "layoutDescriptor:name",
        "config": {},
        "regions": {
            "left": {
                "components": [
                    {
                        "path": "/left/0",
                        "type": "part",
                        "config": {}
                    },
                    {
                        "path": "/left/1",
                        "type": "text",
                        "text": "text text text"
                    },
                    {
                        "path": "/left/2",
                        "type": "text",
                        "text": ""
                    }
                ]
            },
            "right": {
                "components": [
                    {
                        "path": "/right/0",
                        "type": "image"
                    },
                    {
                        "path": "/right/1",
                        "type": "fragment",
                        "fragment": "213sda-ss222"
                    }
                ]
            }
        }
    }
};

exports.getById = function () {
    var result = content.get({
        key: '123456'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getByIdWithPageAsFragment = function () {
    var result = content.get({
        key: '123456'
    });

    assert.assertJsonEquals(pageAsFragmentJson.fragment, result.fragment);
};

exports.getByPath = function () {
    var result = content.get({
        key: '/a/b/mycontent'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getById_notFound = function () {
    var result = content.get({
        key: '123456'
    });

    assert.assertNull(result);
};

exports.getByPath_notFound = function () {
    var result = content.get({
        key: '/a/b/mycontent'
    });

    assert.assertNull(result);
};
