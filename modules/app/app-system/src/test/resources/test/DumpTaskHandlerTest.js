var system = require('/tasks/dump/dump.js');

exports.dump = function () {
    system.run({name: 'dump', includeVersions: true, maxAge: 10, maxVersions: 20}, "task");
};

exports.dumpRepositories = function () {
    system.run({name: 'dump', includeVersions: true, repositories: ['my-repo', 'other-repo']}, "task");
};
