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

assert.assertEquals('ApiUrlParams{type=server, params={a=[1], b=[2]}, descriptorKey=com.enonic.app.myapp:myapi, path=mypath/subpath}', url);
assert.assertEquals('ApiUrlParams{type=server, params={}, descriptorKey=com.enonic.app.myapp:myapi, pathSegments=[mypath, myotherpath]}',
    apiUrlWithPathSegments);
assert.assertEquals('ApiUrlParams{type=server, params={}, descriptorKey=com.enonic.app.myapp:myapi}', apiUrl);
assert.assertEquals('ApiUrlParams{type=server, params={}, descriptorKey=myapplication:myapi, baseUrl=https://example.com}', apiUrlWithBaseUrl);
