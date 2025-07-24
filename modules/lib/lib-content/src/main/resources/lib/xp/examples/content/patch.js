var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Editor to call for content.
function patcher(c) {
    c.displayName = 'Modified';
    c.language = 'en';
    c.data.myCheckbox = false;
    c.data['myTime'] = '11:00';
    c.publish.from = '2016-11-03T10:01:34Z';
    c.publish.to = '2016-11-03T11:01:34Z';
    c.publish.first = '2016-11-03T10:00:34Z';
    c.workflow.state = 'READY';
    c.workflow.checks = {
        'Review by marketing': 'APPROVED'
    };

    c.childOrder = '_ts DESC, _name DESC';
    return c;
}

// Update content by path
var result = contentLib.patch({
    key: '/a/b/mycontent',
    patcher: patcher
});

if (result) {
    log.info('Content patched. New title is ' + result.displayName);
} else {
    log.info('Content not found');
}
// END

// BEGIN
// Content patched.
var expected = {
    "contentId": "123456",
    "results": [
        {
            "branch": "draft",
            "content": {
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
                "childOrder": "_ts DESC, _name DESC",
                "data": {
                    "myfield": "Hello World",
                    "myCheckbox": false,
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
                    "from": "2016-11-03T10:01:34Z",
                    "to": "2016-11-03T11:01:34Z",
                    "first": "2016-11-03T10:00:34Z"
                },
                "workflow": {
                    "state": "READY",
                    "checks": {
                        "Review by marketing": "APPROVED"
                    }
                }
            }
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
