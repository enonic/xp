var contentLib = require('/lib/xp/content');

// reset content by path
contentLib.resetInheritance({
    key: '/a/b/mycontent',
    projectName: 'child1',
    inherit: ['NAME', 'CONTENT']
});

// reset content by id
contentLib.resetInheritance({
    key: 'mycontent-id',
    projectName: 'child2',
    inherit: ['SORT']
});

