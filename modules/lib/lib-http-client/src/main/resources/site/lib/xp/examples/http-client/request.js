var httpClientLib = require('/lib/xp/http-client');
var assert = require('/lib/xp/assert');

function getServerHost() {
    return testInstance.getServerHost();
}

// BEGIN
var response = httpClientLib.request({
    url: 'http://' + getServerHost() + '/my/service',
    method: 'POST',
    headers: {
        'X-Custom-Header': 'header-value'
    },
    connectionTimeout: 20000,
    readTimeout: 5000,
    body: '{"id": 123}',
    contentType: 'application/json'
});
// END

// BEGIN
// Expected result from request.
var expected = {
    'status': 200,
    'message': 'OK',
    'body': 'POST request',
    'headers': {
        'Content-Length': '12'
    }
};
// END

assert.assertJsonEquals(expected, response);
