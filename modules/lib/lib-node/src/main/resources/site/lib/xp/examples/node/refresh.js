var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');


var repo = nodeLib.connect({
    repoId: "cms-repo",
    branch: "master"
});

// BEGIN
// Refresh repository indices
repo.refresh();
// END

// BEGIN
// Refresh repository indices
repo.refresh('SEARCH');
// END