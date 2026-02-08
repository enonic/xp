var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.assetUrl({
    path: 'styles/main.css'
});
// END

assert.assertTrue(url.indexOf('/site/mocksite/_/asset/styles/main.css') === 0);
