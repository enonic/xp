var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.url({
    path: '/site/master/mysite',
    params: {
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('GenerateUrlParams{type=server, params={a=[1], b=[2]}, path=/site/master/mysite}', url);
