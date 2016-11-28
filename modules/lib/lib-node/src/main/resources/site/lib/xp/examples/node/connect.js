var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');


// BEGIN
// Fetches a node.
var myRepo = nodeLib.connect({
    repoId: 'myRepo',
    branch: 'master'
});

myRepo.create({
    _name: "myName",
    displayName: "This is brand new node"
});


// END

//assert.assertJsonEquals(expected, result1);
//assert.assertJsonEquals(2, result2.length);
//assert.assertJsonEquals(expected, result2[0]);
