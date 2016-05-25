var httpClientLib = require('/lib/xp/http-client');
var assert = require('/lib/xp/assert');

function getServerHost() {
    return testInstance.getServerHost();
}

// BEGIN
var response = httpClientLib.request({
    url: 'http://' + getServerHost() + '/uploadMedia',
    method: 'POST',
    contentType: 'multipart/mixed',
    multipart: [
        {
            name: 'media',
            fileName: 'logo.png',
            contentType: 'image/png',
            value: myImageStream
        },
        {
            name: 'category',
            value: 'images'
        }
    ]
});
// END
