var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Retrieves a repository
var result = repoLib.get('test-repo');

if (result) {
    log.info('Repository found');
} else {
    log.info('Repository was not found');
}
// END

// BEGIN
// Repository retrieved.
var expected = {
    'id': 'test-repo',
    'transient': false,
    'branches': [
        'master'
    ],
    data: {}
};
// END
assert.assertJsonEquals(expected, result);
