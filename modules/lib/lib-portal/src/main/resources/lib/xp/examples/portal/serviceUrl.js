var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.serviceUrl({
    service: 'myservice',
    params: {
        'å': 'a',
        'ø': 'o',
        'æ': ['a', 'e'],
        'empty': ''
    }
});
// END

assert.assertEquals('ServiceUrlParams{type=server, params={å=[a], ø=[o], æ=[a, e], empty=[]}, service=myservice}', url);
