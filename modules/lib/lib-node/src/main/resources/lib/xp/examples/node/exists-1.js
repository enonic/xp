var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');


// BEGIN
// Fetches a node.
var myRepo = nodeLib.connect({
    repoId: 'my-repo',
    branch: 'master'
});

var result = myRepo.exist(
    "/my-name"
);

// END


assert.assertTrue(result);


