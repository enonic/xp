var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.updateLanguage = function () {
    var result = content.updateMetadata({
        key: '123456',
        editor: function (c) {
            c.language = 'en';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('en', result.content.language);
};

exports.updateOwner = function () {
    var result = content.updateMetadata({
        key: '123456',
        editor: function (c) {
            c.owner = 'user:system:new-owner';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('user:system:new-owner', result.content.owner);
};

exports.updateLanguageAndOwner = function () {
    var result = content.updateMetadata({
        key: '123456',
        editor: function (c) {
            c.language = 'en';
            c.owner = 'user:system:new-owner';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('en', result.content.language);
    assert.assertEquals('user:system:new-owner', result.content.owner);
};
