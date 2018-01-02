var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.applicationUrl({
    application: app.name,
    path: '/subpath',
    params: {
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('ApplicationUrlParams{type=server, params={a=[1], b=[2]}, path=/subpath, application=myapplication}', url);
