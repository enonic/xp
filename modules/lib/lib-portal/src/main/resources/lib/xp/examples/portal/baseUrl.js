const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const urlById = portalLib.baseUrl({
    type: 'server',
    id: 'contentId'
});

const urlByPath = portalLib.baseUrl({
    type: 'server',
    path: '/path'
});

// END

assert.assertEquals('/site/mocksite', urlById);
assert.assertEquals('/site/mocksite', urlByPath);
