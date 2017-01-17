var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a branch
try {
    var result = repoLib.createBranch({
        branchId: 'test-branch',
        repoId: 'my-repo'
    });
    log.info('Branch [' + result.id + '] created');
} catch (e) {
    if (e.code == 'branchAlreadyExists') {
        log.error('Branch [features-branch] already exist');
    } else {
        log.error('Unexpected error: ' + e.message);
    }
}
// END
assert.assertJsonEquals({id: 'test-branch'}, result);