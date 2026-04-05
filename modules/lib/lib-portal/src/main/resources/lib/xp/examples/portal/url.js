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

assert.assertEquals('GenerateUrlParams{type=server, params={a=[1], b=[2]}, path=/site/master/mysite}', url);
assert.assertEquals('GenerateUrlParams{type=server, params={a=[1], b=[2]}, pathSegments=[site, master, mysite]}', urlBasedOnPathSegments);

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
