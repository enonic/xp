var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.componentUrl({
    component: 'main/0'
});
// END

assert.assertEquals('ComponentUrlParams{type=server, params={}, component=main/0}', url);
