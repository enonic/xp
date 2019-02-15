var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');


var repo = nodeLib.connect({
    repoId: "com.enonic.cms.default",
    branch: "master"
});

// BEGIN
// Refresh repository indices
repo.refresh();
// END

// BEGIN
// Refresh repository search index
repo.refresh('SEARCH');
// END