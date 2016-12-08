var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Retrieves a repository
var result = repoLib.delete('test-repo');

if (result) {
    log.info('Repository deleted');
} else {
    log.info('Repository was not found');
}
// END

assert.assertJsonEquals(true, result);