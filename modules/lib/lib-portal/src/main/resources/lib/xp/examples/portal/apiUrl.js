const portalLib = require('/lib/xp/portal');

// BEGIN
let url = portalLib.apiUrl({
    api: 'com.enonic.app.myapp:myapi',
    params: {
        'å': 'a',
        'ø': 'o',
        'æ': ['a', 'e'], // Arrays are supported and will be converted to multiple query parameters with the same name.
        'empty': '', // Empty string is supported and will be included as a query parameter with an empty value.
        'no-value': [], // Empty array makes a parameter included without a value. `?no-value`
    },
    path: ['segment1', 'segment2'],
    baseUrl: 'https://example.com',
});
// END
