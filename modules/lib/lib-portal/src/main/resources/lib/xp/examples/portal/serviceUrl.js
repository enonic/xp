var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.serviceUrl({
    service: 'myservice',
    params: {
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('/site/mocksite/_/service/myservice', url);
