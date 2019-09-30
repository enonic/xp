const repoLib = require('/lib/xp/repo');
const assert = require('/lib/xp/testing');


// BEGIN
var binaryStream = repoLib.getBinary({
    repoId: "my-repo",
    binaryReference: "myBinaryReference"
});

// END

assert.assertNotNull(binaryStream);


