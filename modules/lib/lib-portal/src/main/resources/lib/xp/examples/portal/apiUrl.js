const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const url = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
    api: 'myapi',
    params: {
        a: 1,
        b: 2
    }
});

const unnamedApiUrl = portalLib.apiUrl({
    application: 'com.enonic.app.myapp',
});

// END

assert.assertEquals('ApiUrlParams{type=server, params={a=[1], b=[2]}, api=myapi, application=com.enonic.app.myapp}', url);
assert.assertEquals('ApiUrlParams{type=server, params={}, application=com.enonic.app.myapp}', unnamedApiUrl);
