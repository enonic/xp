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

assert.assertEquals('/site/mocksite/_/api/com.enonic.app.myapp:myapi', url);
assert.assertEquals('/site/mocksite/_/api/com.enonic.app.myapp:myapi', apiUrlWithPathSegments);
assert.assertEquals('/site/mocksite/_/api/com.enonic.app.myapp:myapi', apiUrl);
assert.assertEquals('/site/mocksite/_/api/myapplication:myapi', apiUrlWithBaseUrl);
