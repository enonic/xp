var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.updateLanguage = function () {
    var result = content.updateMetadata({
        key: '123456',
        language: 'en'
    });

    assert.assertEquals('123456', result.contentId);
    assert.assertEquals('en', result.results[0].content.language);
};

exports.updateOwner = function () {
    var result = content.updateMetadata({
        key: '123456',
        owner: 'user:system:new-owner'
    });

    assert.assertEquals('123456', result.contentId);
    assert.assertEquals('user:system:new-owner', result.results[0].content.owner);
};

exports.updateLanguageAndOwner = function () {
    var result = content.updateMetadata({
        key: '123456',
        language: 'no',
        owner: 'user:system:new-owner'
    });

    assert.assertEquals('123456', result.contentId);
    assert.assertEquals('no', result.results[0].content.language);
    assert.assertEquals('user:system:new-owner', result.results[0].content.owner);
};
