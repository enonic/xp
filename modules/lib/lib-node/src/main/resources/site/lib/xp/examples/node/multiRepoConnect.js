var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');


// BEGIN
// Connect to repo 'myRepo', branch 'master'.
var searchConnection = nodeLib.multiRepoConnect({
    sources: [
        {
            repoId: 'my-repo',
            branch: 'myBranch',
            principals: ["role:system.admin"]
        },
        {
            repoId: 'my-other-repo',
            branch: 'master',
            principals: ["role:system.admin"]
        }
    ]
});
// END

assert.assertNotNull(searchConnection);