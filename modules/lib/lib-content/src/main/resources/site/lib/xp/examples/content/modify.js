var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Editor to call for content.
function editor(c) {
    c.displayName = 'Modified';
    c.language = 'en';
    c.data.myCheckbox = false;
    c.data["myTime"] = "11:00";
    c.publish.from = "2016-11-03T10:01:34Z";
    return c;
}

// Modify content by path
var result = contentLib.modify({
    key: '/a/b/mycontent',
    editor: editor
});

if (result) {
    log.info('Content modified. New title is ' + result.displayName);
} else {
    log.info('Content not found');
}
// END

// BEGIN
// Content modified.
var expected = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/path/to/mycontent",
    "creator": "user:system:admin",
    "modifier": "user:system:admin",
    "createdTime": "1970-01-01T00:00:00Z",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "type": "base:unstructured",
    "displayName": "Modified",
    "hasChildren": false,
    "language": "en",
    "valid": true,
    "childOrder": "_timestamp DESC, _name ASC",
    "data": {
        "myfield": "Hello World",
        "myCheckbox": "false",
        "myTime": "11:00"
    },
    "x": {},
    "page": {},
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
        "from": "2016-11-03T10:01:34Z"
    }
};
// END

assert.assertJsonEquals(expected, result);
