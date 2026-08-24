var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.flipToManual = function () {

    var result = contentLib.sort({
        key: '/my-site/my-list',
        childOrder: 'manual'
    });

    assert.assertEquals('123456', result.content._id);
    assert.assertEquals('3ala4x8dnbc.123456', result.content._orderKey);
    assert.assertEquals(0, result.movedChildren.length);
};

exports.sortByExpression = function () {

    var result = contentLib.sort({
        key: '123456',
        childOrder: 'displayName ASC'
    });

    assert.assertEquals('123456', result.content._id);
};

exports.reorderWithAnchors = function () {

    var result = contentLib.sort({
        key: '123456',
        reorder: [
            {contentId: 'child-1', afterOrderKey: '3a00000zzzz.above', beforeOrderKey: '3a00001zzzz.below'},
            {contentId: 'child-2', afterOrderKey: '3a00001zzzz.below'}
        ]
    });

    assert.assertEquals(2, result.movedChildren.length);
    assert.assertEquals('child-1', result.movedChildren[0]);
    assert.assertEquals('child-2', result.movedChildren[1]);
};

exports.missingParams = function () {

    var threw = false;
    try {
        contentLib.sort({key: '123456'});
    } catch (e) {
        threw = true;
    }

    assert.assertEquals(true, threw);
};
