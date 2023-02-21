var system = require('/tasks/audit-log-cleanup/audit-log-cleanup.js');

exports.cleanUp = function () {
    let params = {ageThreshold: 'PT2s'};
    system.run(params, "id1");
};
