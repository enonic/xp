var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var url = portalLib.serviceUrl({
    service: 'myservice',
    params: {
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('ServiceUrlParams{type=server, params={a=[1], b=[2]}, service=myservice}', url);
