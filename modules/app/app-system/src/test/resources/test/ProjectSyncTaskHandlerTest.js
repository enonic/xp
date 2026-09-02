var system = require('/tasks/project-sync/project-sync.js');

exports.sync = function () {
    system.run({projects: ['child2']}, "task");
};

exports.syncAll = function () {
    system.run({}, "task");
};
