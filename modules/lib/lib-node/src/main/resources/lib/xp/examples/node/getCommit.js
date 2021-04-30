var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

/* global log */

var repo = nodeLib.connect({
    repoId: 'cms-repo',
    branch: 'draft'
});

// BEGIN
// Commits a node.
var commit = repo.commit({
    keys: ['nodeId'],
    message: 'Commit message'
});
// END

// BEGIN
// Get commit.
var result = repo.getCommit({
    id: commit.id
});

log.info('Commit created with id ' + result._id);
// END


assert.assertJsonEquals(commit, result);
