const assert = require('/lib/xp/testing.js');
const content = require('/lib/xp/content.js');

exports.languageInEditorMapIsSilentlyIgnored = () => {
    let result = content.updateMetadata({
        key: '123456',
        editor: c => {
            c.language = 'ja'; // silently dropped: modifyMetadata no longer touches language
            c.owner = 'user:system:new-owner';
            return c;
        }
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('user:system:new-owner', result.content.owner);
    // language stays at the source content's value (TestDataFixtures.newSmallContent => 'en'), not 'ja'
    assert.assertEquals('en', result.content.language);
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
