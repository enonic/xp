const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const url = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    api: 'myapi',
    params: {
        a: 1,
        b: 2
    },
    path: 'mypath/subpath'
});

const apiUrlWithPathSegments = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    api: 'myapi',
    path: ['mypath', 'myotherpath']
});

const apiUrl = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    api: 'myapi',
});

const apiUrlWithBaseUrl = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    api: 'myapi',
    baseUrl: 'https://example.com',
});

// END

assert.assertEquals('ApiUrlParams{type=server, params={a=[1], b=[2]}, api=myapi, application=com.enonic.app.myapp, path=mypath/subpath}', url);
assert.assertEquals('ApiUrlParams{type=server, params={}, api=myapi, application=com.enonic.app.myapp, pathSegments=[mypath, myotherpath]}', apiUrlWithPathSegments);
assert.assertEquals('ApiUrlParams{type=server, params={}, api=myapi, application=com.enonic.app.myapp}', apiUrl);
assert.assertEquals('ApiUrlParams{type=server, params={}, api=myapi, application=com.enonic.app.myapp, baseUrl=https://example.com}', apiUrlWithBaseUrl);
