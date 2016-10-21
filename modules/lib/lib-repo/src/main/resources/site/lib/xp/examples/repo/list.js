var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Retrieves the list of repositories
var result = repoLib.list();
log.info(result.length + ' repositories found');
// END

// BEGIN
// Repositories retrieved.
var expected = [{
    "id": "test-repo",
    "branches": [
        "master"
    ],
    settings: {}
}, {
    "id": "another-repo",
    "branches": [
        "master"
    ],
    settings: {}
}];
// END
assert.assertJsonEquals(expected, result);