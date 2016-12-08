var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

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
    "id": "test-repo",
    "branches": [
        "master"
    ],
    settings: {}
};
// END
assert.assertJsonEquals(expected, result);