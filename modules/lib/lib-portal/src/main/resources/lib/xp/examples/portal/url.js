const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const url = portalLib.url({
    path: '/site/master/mysite',
    params: {
        a: 1,
        b: 2
    }
});

const urlBasedOnPathSegments = portalLib.url({
    path: ['site', 'master', 'mysite'],
    params: {
        a: 1,
        b: 2
    }
});

// END

assert.assertEquals('/site/mocksite/_/generated//site/master/mysite', url);
assert.assertEquals('/site/mocksite/_/generated/null', urlBasedOnPathSegments);

try {
    portalLib.url({
        path: false,
        params: {
            a: 1,
            b: 2
        }
    });
} catch (e) {
    assert.assertTrue('java.lang.IllegalArgumentException: Invalid path value', `${e}`);
}
