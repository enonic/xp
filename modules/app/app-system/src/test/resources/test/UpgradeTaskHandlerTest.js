var system = require('/tasks/upgrade/upgrade.js');

exports.upgrade = function () {
    system.run({name: 'dump-name'}, "task");
};
