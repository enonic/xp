var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Retrieves a repository
var result = repoLib.list();
log.info(result.length + ' repositories found');
// END

// BEGIN
// Repository retrieved.
var expected = [{
    "id": "test-repo",
    settings: {}
}, {
    "id": "another-repo",
    settings: {}
}];
// END
assert.assertJsonEquals(expected, result);