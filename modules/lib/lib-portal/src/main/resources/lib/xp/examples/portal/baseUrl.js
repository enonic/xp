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

assert.assertEquals('BaseUrlParams{type=server, path=contentId, project=explicit-project, branch=explicit-branch}', urlById);
assert.assertEquals('BaseUrlParams{type=server, path=/path, project=explicit-project, branch=explicit-branch}', urlByPath);
