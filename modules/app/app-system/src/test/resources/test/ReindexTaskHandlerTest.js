var system = require('/tasks/reindex/reindex.js');

exports.reindex = function () {
    system.run({repository: 'my-repo', branches: ['master'], initialize: true}, "task");
};

exports.reindexDefault = function () {
    system.run({repository: 'my-repo'}, "task");
};
