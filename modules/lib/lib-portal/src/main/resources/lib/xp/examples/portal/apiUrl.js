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

const unnamedApiUrl = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    path: ['mypath', 'myotherpath']
});

const apiUrl = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    api: 'myapi',
});

// END

assert.assertEquals('ApiUrlParams{type=server, params={a=[1], b=[2]}, api=myapi, application=com.enonic.app.myapp, path=mypath/subpath}', url);
assert.assertEquals('ApiUrlParams{type=server, params={}, application=com.enonic.app.myapp, pathSegments=[mypath, myotherpath]}', unnamedApiUrl);
assert.assertEquals('ApiUrlParams{type=server, params={}, api=myapi, application=com.enonic.app.myapp}', apiUrl);
