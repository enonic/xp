var system = require('/tasks/export/export.js');

exports.exportNodes = function () {
    system.run({repository: 'a', branch: 'b', nodePath: '/c', exportName: 'export'}, "task");
};

exports.exportNodesBatch = function () {
    system.run({repository: 'a', branch: 'b', nodePath: '/c', exportName: 'export', batchSize: 50}, "task");
};
