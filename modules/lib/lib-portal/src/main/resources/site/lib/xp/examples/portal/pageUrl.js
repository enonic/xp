var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var url = portalLib.pageUrl({
    path: '/my/page',
    params: {
        a: 1,
        b: [1, 2]
    }
});
// END

assert.assertEquals('PageUrlParams{type=server, params={a=[1], b=[1, 2]}, path=/my/page}', url);
