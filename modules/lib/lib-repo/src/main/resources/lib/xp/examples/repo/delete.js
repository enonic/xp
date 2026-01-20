const repoLib = require('/lib/xp/repo.js');
const t = require('/lib/xp/testing');

// BEGIN
// Retrieves a repository
var result = repoLib.deleteRepo('test-repo');

if (result) {
    log.info('Repository deleted');
} else {
    log.info('Repository was not found');
}
// END

t.assertJsonEquals(true, result);