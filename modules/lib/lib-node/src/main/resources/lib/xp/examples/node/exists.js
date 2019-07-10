var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');


// BEGIN
// Checks if a node exists
var myRepo = nodeLib.connect({
    repoId: 'my-repo',
    branch: 'master'
});

var result = myRepo.exist(
    '/path/to/mynode'
);

// END


assert.assertTrue(result);


