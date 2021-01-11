var t = require('/lib/xp/testing');

t.mock('/lib/xp/something', {
    a: 1,
    b: 2
});

t.mock('/lib/xp/content', {
    get(params) {
        return {
            '_id': 'id'
        }
    }
});

t.mock('/lib/xp/portal.js', {
    assetUrl(params) {
        return 'generated_url';
    }
});

exports.testMock = function () {
    var mock = require('/lib/xp/something');
    t.assertEquals(1, mock.a);
    t.assertEquals(2, mock.b);
};

exports.testMockLibContent = function () {
    var libContent = require('/lib/xp/content');

    var result = libContent.get({
        key: 'id'
    });

    t.assertEquals("id", result['_id']);
};

exports.testMockLibPortal = function () {
    var libPortal = require('/lib/xp/portal');

    var result = libPortal.assetUrl({
        id: 'id'
    });

    t.assertEquals("generated_url", result);
};
