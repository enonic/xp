/* global require, Java, testInstance*/
const repoLib = require('/lib/xp/repo.js');
const valueLib = require('/lib/xp/value');

const stream = testInstance.createByteSource('Hello World');

// Editor to call for repo.
function editor(repoData) {

    repoData.myString = 'modified';
    repoData.myArray = ['modified1', 'modified2', 'modified3'];

    repoData.myBinaryReference = valueLib.binary('myFile', stream);

    delete repoData.toBeRemoved;

    return repoData;
}

// BEGIN
// Update data
const result = repoLib.modify({
    editor: editor,
    id: 'my-repo'
});
// END