var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var url = portalLib.rewriteUrl({
    url: '/admin',
    params: {
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('RewriteUrlParams{type=server, params={a=[1], b=[2]}, url=/admin}', url);
