var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Deletes a branch
try {
    var result = repoLib.deleteBranch({
        branchId: 'test-branch',
        repoId: 'my-repo'
    });
    log.info('Branch [' + result.id + '] deleted');
} catch (e) {
    if (e.code == 'branchNotFound') {
        log.error('Branch [test-branch] does not exist');
    } else {
        log.error('Unexpected error: ' + e.message);
    }
}
// END
assert.assertJsonEquals({id: 'test-branch'}, result);