var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.imageUrl({
    id: '1234',
    project: 'myproject',
    branch: 'mybranch',
    baseUrl: 'mybaseUrl',
    scale: 'block(1024,768)',
    filter: 'rounded(5);sharpen()',
});
// END

assert.assertEquals('/site/mocksite/_/image/1234', url);
