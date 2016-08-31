var repoLib = require('/lib/xp/repo.js');

// BEGIN
// Refresh all for default repository
repoLib.refresh();
// END

// BEGIN
// Refresh storage for default repository
repoLib.refresh({mode: 'storage'});
// END

// BEGIN
// Refresh search for 'system-repo' repository
repoLib.refresh({
    mode: 'search',
    repo: 'system-repo'
});
// END
