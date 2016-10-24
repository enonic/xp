var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Retrieves a repository
var result = repoLib.createBranch({
    id: 'test-branch'
});

if (result) {
    log.info('Branch created');
}
// END

assert.assertJsonEquals(true, result);