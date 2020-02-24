/* global Java, testInstance*/
const repoLib = require('/lib/xp/repo.js');


// Editor to call for repo.
function editor(repoData) {
    return null;
}

// BEGIN
// Update data
const result = repoLib.modify({
    scope: 'myScopedObject',
    editor: editor,
    id: 'my-repo'
});
// END
