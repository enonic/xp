var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Duplicates a content.
var result = contentLib.duplicate({
    contentId: '123456',
    includeChildren: false,
    variant: true,
    name: 'variant-name'
});
// END

assert.assertJsonEquals({
    "contentName": "variant-name",
    "sourceContentPath": "/path/to/variant-name",
    "duplicatedContents": [
        "123456"
    ]
}, result);
