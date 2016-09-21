var repoLib = require('/lib/xp/repo.js');

// BEGIN
// Tests if a repository is initialized
var result = repoLib.get({
    id: 'test-repo'
});

if (result) {
    log.info('Repository found');
} else {
    log.info('Repository was not found');
}
// END