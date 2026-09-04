var system = require('/tasks/import/import.js');

exports.importNodes = function () {
    system.run({exportName: 'export', repository: 'system-repo', branch: 'master', nodePath: '/a'}, "task");
};

exports.importNodesWithoutIds = function () {
    system.run({exportName: 'export', repository: 'system-repo', branch: 'master', nodePath: '/a', importWithIds: false}, "task");
};
