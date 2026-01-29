var nodeLib = require('/lib/xp/node');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

// Editor that does not return anything (returns undefined)
function editor(node) {
    node.myString = 'modified';
    // Missing return statement - should cause an error
}

// This should throw an error because the editor doesn't return the node
repo.modify({
    key: 'abc',
    editor: editor
});
