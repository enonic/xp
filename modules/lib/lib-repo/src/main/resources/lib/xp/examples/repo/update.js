/* global require */
var repoLib = require('/lib/xp/repo.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Update data
var result = repoLib.updateRepository({
    data: {'someData': 'someValue'},
    id: 'my-repo'
});
var expected = {
    id: 'my-repo',
    branches: [
        'master'
    ],
    settings: {},
    data: {'someData': 'someValue'}
};
// END
assert.assertJsonEquals(expected, result);