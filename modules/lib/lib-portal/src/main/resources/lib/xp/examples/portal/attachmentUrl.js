var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.attachmentUrl({
    id: '1234',
    project: 'myproject',
    branch: 'mybranch',
    baseUrl: 'mybaseUrl',
    download: true,
});
// END

assert.assertEquals(
    'AttachmentUrlParams{type=server, params={}, id=1234, project=myproject, branch=mybranch, baseUrl=mybaseUrl, download=true}', url);
