var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.duplicate = function () {
    var result = contentLib.duplicate({
        contentId: '123456',
        includeChildren: false,
    });

    assert.assertJsonEquals({
        "contentName": "mycontent",
        "sourceContentPath": "/path/to/mycontent",
        "duplicatedContents": [
            "123456"
        ]
    }, result);
};

exports.duplicateAsVariant = function () {
    var result = contentLib.duplicate({
        contentId: '123456',
        includeChildren: false,
        variant: true,
        name: 'variantName'
    });

    assert.assertJsonEquals({
        "contentName": "variant-name",
        "sourceContentPath": "/path/to/variant-name",
        "duplicatedContents": [
            "123456"
        ]
    }, result);
};
