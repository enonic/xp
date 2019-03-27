var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.componentUrl({
    component: 'main/0'
});

assert.assertEquals('ComponentUrlParams{type=server, params={}, component=main/0}', url);
// END

//check null params
url = portalLib.componentUrl();

assert.assertEquals('ComponentUrlParams{type=server, params={}}', url);

//check empty params
url = portalLib.componentUrl({});

assert.assertEquals('ComponentUrlParams{type=server, params={}}', url);
