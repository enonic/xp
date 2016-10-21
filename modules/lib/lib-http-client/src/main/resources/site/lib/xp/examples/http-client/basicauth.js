var httpClientLib = require('/lib/xp/http-client');
var assert = require('/lib/xp/assert');

function getServerHost() {
    return testInstance.getServerHost();
}

var host = getServerHost();

// BEGIN
// request using basic authentication
var response = httpClientLib.request({
    url: 'http://' + host + '/protected/service',
    method: 'GET',
    auth: {
        user: 'username',
        password: 'secret'
    }
});
// END
