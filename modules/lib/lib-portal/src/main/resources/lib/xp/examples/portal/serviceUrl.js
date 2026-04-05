var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.serviceUrl({
    service: 'myservice',
    params: {
        'å': 'a',
        'ø': 'o',
        'æ': ['a', 'e'],
        'empty': '',
        a: 1,
        b: 2
    }
});
// END

assert.assertEquals('/site/mocksite/_/service/myservice?%C3%A5=a&%C3%B8=o&%C3%A6=a&%C3%A6=e&empty=&a=1&b=2', url);
