var httpClientLib = require('/lib/xp/http-client');
var assert = require('/lib/xp/assert');

function getServerHost() {
    return testInstance.getServerHost();
}

var host = getServerHost();

// BEGIN
// request using a proxy with authentication
var response = httpClientLib.request({
    url: 'http://' + host + '/some/service',
    method: 'GET',
    proxy: {
        host: '172.16.0.42',
        port: 8080,
        user: 'admin',
        password: 'secret'
    }
});
// END
