const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const url = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
    params: {
        a: 1,
        b: 2
    },
    path: 'mypath/subpath'
});

const apiUrlWithPathSegments = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
    path: ['mypath', 'myotherpath']
});

const apiUrl = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
});

const apiUrlWithBaseUrl = portalLib.apiUrl({
    api: 'myapi',
    baseUrl: 'https://example.com',
});

// END

assert.assertTrue(url.indexOf('/site/mocksite/_/api/') === 0);
assert.assertTrue(apiUrlWithPathSegments.indexOf('/site/mocksite/_/api/') === 0);
assert.assertTrue(apiUrl.indexOf('/site/mocksite/_/api/') === 0);
assert.assertTrue(apiUrlWithBaseUrl.indexOf('/site/mocksite/_/api/') === 0);
