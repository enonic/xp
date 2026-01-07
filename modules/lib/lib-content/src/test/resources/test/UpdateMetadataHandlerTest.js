var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.updateLanguage = function () {
    var result = content.updateMetadata({
        key: '123456',
        editor: function (c) {
            c.language = 'ja';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('ja', result.content.language);
};

exports.unsetLanguage = function () {
    var result = content.updateMetadata({
        key: '123456',
        editor: function (c) {
            c.language = null;
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertNull( result.content.language );
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
    assert.assertEquals('en', result.content.language);
};

exports.updateLanguageAndOwner = function () {
    var result = content.updateMetadata({
        key: '123456',
        editor: function (c) {
            c.language = 'ja';
            c.owner = 'user:system:new-owner';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('ja', result.content.language);
    assert.assertEquals('user:system:new-owner', result.content.owner);
};
