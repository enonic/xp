const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
const urlById = portalLib.baseUrl({
    type: 'server',
    path: 'contentId',
    project: 'explicit-project',
    branch: 'explicit-branch',
});

const urlByPath = portalLib.baseUrl({
    type: 'server',
    path: '/path',
    project: 'explicit-project',
    branch: 'explicit-branch',
});

// END

assert.assertTrue(urlById.indexOf('/site/mocksite') === 0);
assert.assertTrue(urlByPath.indexOf('/site/mocksite') === 0);
