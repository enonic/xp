var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Retrieves a repository
var result = repoLib.get({
    id: 'test-repo'
});

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
    settings: {
        validationSettings: {
            checkExists: true,
            checkParentExists: true
        }
    }
};
// END
assert.assertJsonEquals(expected, result);