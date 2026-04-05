const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const url = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
    params: {
        'å': 'a',
        'ø': 'o',
        'æ': ['a', 'e'],
        'empty': '',
        a: 1,
        b: 2
    },
    path: ['segment1', 'segment2'],
    baseUrl: 'https://example.com',
});

const apiUrlWithPathSegments = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
    path: ['mypath', 'myotherpath']
});

const apiUrl = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
});

const apiUrlWithBaseUrl = portalLib.apiUrl({
    api: 'myapi',
    baseUrl: 'https://example.com',
});

// END

assert.assertEquals('/site/mocksite/_/api/com.enonic.app.myapp:myapi?%C3%A5=a&%C3%B8=o&%C3%A6=a&%C3%A6=e&empty=&a=1&b=2', url);
assert.assertEquals('/site/mocksite/_/api/com.enonic.app.myapp:myapi', apiUrlWithPathSegments);
assert.assertEquals('/site/mocksite/_/api/com.enonic.app.myapp:myapi', apiUrl);
assert.assertEquals('/site/mocksite/_/api/myapplication:myapi', apiUrlWithBaseUrl);
