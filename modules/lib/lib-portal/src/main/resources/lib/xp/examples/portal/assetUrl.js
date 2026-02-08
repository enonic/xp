var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.assetUrl({
    path: 'styles/main.css'
});
// END

assert.assertEquals('/site/mocksite/_/asset/styles/main.css', url);
