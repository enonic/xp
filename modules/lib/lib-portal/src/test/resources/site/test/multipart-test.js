var assert = require('/lib/xp/assert.js');
var portal = require('/lib/xp/portal.js');

exports.getForm = function () {
    var expected = {
        "item1": {
            "name": "item1",
            "fileName": "item1.jpg",
            "contentType": "image/png",
            "size": 10
        },
        "item2": {
            "name": "item2",
            "fileName": "item2.jpg",
            "contentType": "image/png",
            "size": 20
        }
    };

    var result = portal.getMultipartForm();
    assert.assertJsonEquals(expected, result);
};

exports.getForm_empty = function () {
    var result = portal.getMultipartForm();
    assert.assertJsonEquals({}, result);
};

exports.getBytes = function () {
    var result = portal.getMultipartBytes("item1");
    assert.assertNotNull(result);
};

exports.getBytes_notFound = function () {
    var result = portal.getMultipartBytes("item1");
    assert.assertNull(result);
};
