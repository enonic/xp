/* global Java, testInstance*/
const repoLib = require('/lib/xp/repo.js');
const valueLib = require('/lib/xp/value');
const stream = testInstance.createByteSource('Hello World');

// Editor to call for repo.
function editor(repo) {

    repo.data.myString = 'modified';
    repo.data.myArray = ['modified1', 'modified2', 'modified3'];

    repo.data.myBinaryReference = valueLib.binary('myFile', stream);

    delete repo.data.toBeRemoved;

    return repo;
}

// BEGIN
// Update data
const result = repoLib.modify({
    editor: editor,
    id: 'my-repo'
});
// END
