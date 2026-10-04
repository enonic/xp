var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.baseUrl = function () {
    var base = portal.urlBase({key: '/configured', project: 'myproject', branch: 'master'});

    assert.assertEquals('string', typeof base.baseUrl);
    assert.assertEquals('https://www.example.com', base.baseUrl);
    assert.assertJsonEquals({baseUrl: 'https://www.example.com'}, base);

    portal.pageUrlParts({path: '/configured/post', base: base});
};

exports.noBaseUrl = function () {
    var base = portal.urlBase({key: '/my-site', project: 'myproject', branch: 'master'});

    assert.assertTrue(base.baseUrl === null);
};

exports.baseNotResolved = function () {
    try {
        portal.pageUrlParts({path: '/my-site/post', base: {baseUrl: 'https://www.example.com'}});
    } catch (e) {
        assert.assertEquals("Parameter 'base' must be resolved by urlBase()", e.message);
        return;
    }
    throw new Error('Expected an error');
};
