var assert = require('/lib/xp/testing.js');
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


exports.getFormWithDuplicates = function () {
    var expected = {
        "file1": [
            {
                "name": "file1",
                "fileName": "text123.txt",
                "contentType": "text/plain",
                "size": 10
            },
            {
                "name": "file1",
                "fileName": "text456.txt",
                "contentType": "application/json",
                "size": 42
            }
        ],
        "file2": {
            "name": "file2",
            "fileName": "file2.jpg",
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
    var result = portal.getMultipartStream("item1");
    assert.assertNotNull(result);
};

exports.getBytesMultiple = function () {
    var result1 = portal.getMultipartStream("file1", 0);
    var result2 = portal.getMultipartStream("file1", 1);
    assert.assertNotNull(result1);
    assert.assertNotNull(result2);
    assert.assertTrue(result1 != result2);
};

exports.getBytes_notFound = function () {
    var result = portal.getMultipartStream("item1");
    assert.assertNull(result);
};

exports.getItem = function () {
    var result = portal.getMultipartItem("item1");
    assert.assertNotNull(result);
};

exports.getItemMultiple = function () {
    var result1 = portal.getMultipartItem("file1", 0);
    var result2 = portal.getMultipartItem("file1", 1);
    assert.assertNotNull(result1);
    assert.assertNotNull(result2);
    assert.assertTrue(result1 !== result2);
};

exports.getItem_notFound = function () {
    var result = portal.getMultipartItem("item1");
    assert.assertNull(result);
};

exports.getText = function () {
    var result = portal.getMultipartText("item1");
    assert.assertEquals('Some text', result);
};

exports.getTextMultiple = function () {
    var result1 = portal.getMultipartText("file1", 0);
    var result2 = portal.getMultipartText("file1", 1);
    assert.assertNotNull(result1);
    assert.assertNotNull(result2);
    assert.assertTrue(result1 !== result2);
};

exports.getText_notFound = function () {
    var result = portal.getMultipartText("item1");
    assert.assertNull(result);
};