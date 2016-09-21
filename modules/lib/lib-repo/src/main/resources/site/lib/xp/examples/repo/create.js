var repoLib = require('/lib/xp/repo.js');

// BEGIN
// Creates a repository with default configuration
repoLib.create({
    id: 'test-repo'
});
// END

// BEGIN
// Refresh a repository with check disabled
repoLib.create({
    id: 'test-repo',
    validation: {
        checkExists: false,
        checkParentExists: false
    }
});
// END