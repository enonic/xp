/* global Java, testInstance*/
const repoLib = require('/lib/xp/repo.js');
const valueLib = require('/lib/xp/value');

const stream = testInstance.createByteSource('Hello World');

// Editor to call for repo.
function editor(repoData) {

    repoData.myScopedString = 'modified';
    repoData.myScopedBinaryReference = valueLib.binary('myFile', stream);

    return repoData;
}

// BEGIN
// Update data
const result = repoLib.modify({
    scope: 'myScopedObject',
    editor: editor,
    id: 'my-repo'
});
// END
