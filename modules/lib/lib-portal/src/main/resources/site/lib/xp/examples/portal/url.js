var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var url = portalLib.url({
    path: '/portal/master/mysite',
    params: {
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('UrlParams{type=server, params={a=[1], b=[2]}, url=/admin}', url);
