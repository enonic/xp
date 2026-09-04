var system = require('/tasks/load/load.js');

exports.load = function () {
    system.run({name: 'name'}, "task");
};

exports.loadRepositories = function () {
    system.run({name: 'name', upgrade: true, repositories: ['my-repo']}, "task");
};
