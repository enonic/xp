var repoLib = require('/lib/xp/repo.js');

// BEGIN
// Refresh all
repoLib.refresh();
// END

// BEGIN
// Refresh storage
repoLib.refresh('storage');
// END

// BEGIN
// Refresh search
repoLib.refresh('search');
// END
