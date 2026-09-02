var system = require('/tasks/import/import.js');

exports.importNodes = function () {
    system.run({exportName: 'export', repository: 'system-repo', branch: 'master', nodePath: '/a'}, "task");
};

exports.importNodesXsl = function () {
    system.run({exportName: 'export', repository: 'system-repo', branch: 'master', nodePath: '/a', importWithIds: false,
        xslSource: 't.xsl', xslParams: {k: 'v'}}, "task");
};
