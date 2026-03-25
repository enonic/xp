var system = require('/tasks/restore/restore.js');

exports.restore = function () {
    let params = {snapshotName: 'my-snapshot', repositoryId: 'my-repo', latest: 'false', force: 'true'};
    system.run(params, "task");
};

exports.restoreLatest = function () {
    let params = {latest: 'true'};
    system.run(params, "task");
};
