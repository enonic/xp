var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');


// BEGIN
// Connect to repo 'myRepo', branch 'master'.
var myRepo = nodeLib.connect({
    repoId: 'my-repo',
    branch: 'master',
    principals: ["role:system.admin"]
});

myRepo.create({
    _name: "myName",
    displayName: "This is brand new node"
});
// END

assert.assertNotNull(myRepo);