var system = require('/tasks/snapshot/snapshot.js');

exports.snapshot = function () {
    let params = {snapshotName: 'my-snapshot', repositoryId: 'my-repo'};
    system.run(params, "task");
};

exports.snapshotDefault = function () {
    system.run({}, "task");
};
