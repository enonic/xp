var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.baseUrl = function () {
    var scope = portal.portalScope({key: '/configured', project: 'myproject', branch: 'master'});

    assert.assertEquals('string', typeof scope.baseUrl);
    assert.assertEquals('https://www.example.com', scope.baseUrl);
    assert.assertJsonEquals({baseUrl: 'https://www.example.com'}, scope);

    portal.pageUrlParts({path: '/configured/post', scope: scope});
};

exports.noBaseUrl = function () {
    var scope = portal.portalScope({key: '/my-site', project: 'myproject', branch: 'master'});

    assert.assertTrue(scope.baseUrl === null);
};

exports.baseNotResolved = function () {
    try {
        portal.pageUrlParts({path: '/my-site/post', scope: {baseUrl: 'https://www.example.com'}});
    } catch (e) {
        assert.assertEquals("Parameter 'scope' must be resolved by portalScope()", e.message);
        return;
    }
    throw new Error('Expected an error');
};
