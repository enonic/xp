var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var url = portalLib.assetUrl({
    path: 'styles/main.css'
});
// END

assert.assertEquals('AssetUrlParams{type=server, params={}, path=styles/main.css}', url);
