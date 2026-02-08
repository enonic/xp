var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var url = portalLib.componentUrl({
    component: 'main/0'
});

assert.assertTrue(url.indexOf('/site/mocksite/_/component/main/0') === 0);
// END

//check null params
url = portalLib.componentUrl();

assert.assertTrue(url.indexOf('/site/mocksite/_/component/') === 0);

//check empty params
url = portalLib.componentUrl({});

assert.assertTrue(url.indexOf('/site/mocksite/_/component/') === 0);
