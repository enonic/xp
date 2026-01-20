const assert = require('/lib/xp/testing.js');
const content = require('/lib/xp/content.js');

exports.updateLanguage = () => {
    let result = content.updateMetadata({
        key: '123456',
        editor: c => {
            c.language = 'ja';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('ja', result.content.language);
};

exports.unsetLanguage = () => {
    let result = content.updateMetadata({
        key: '123456',
        editor: c => {
            c.language = null;
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertNull(result.content.language);
};

exports.updateOwner = () => {
    let result = content.updateMetadata({
        key: '123456',
        editor: c => {
            c.owner = 'user:system:new-owner';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('user:system:new-owner', result.content.owner);
    assert.assertEquals('en', result.content.language);
};

exports.updateLanguageAndOwner = () => {
    let result = content.updateMetadata({
        key: '123456',
        editor: c => {
            c.language = 'ja';
            c.owner = 'user:system:new-owner';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('ja', result.content.language);
    assert.assertEquals('user:system:new-owner', result.content.owner);
};

exports.updateVariantOf = () => {
    let result = content.updateMetadata({
            key: '123456',
            editor: c => {
                c.variantOf = '654321';
                return c;
            }
        }
    );
    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('654321', result.content.variantOf);
};