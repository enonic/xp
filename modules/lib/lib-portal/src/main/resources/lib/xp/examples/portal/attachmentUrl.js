var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.attachmentUrl({
    name: '1234.pdf',
    project: 'myproject',
    branch: 'mybranch',
    baseUrl: 'mybaseUrl',
    download: true,
});
// END

assert.assertEquals('/site/mocksite/_/attachment/inline/mockid/1234.pdf', url);
