var system = require('/tasks/vacuum/vacuum.js');

exports.vacuum = function () {
    let params = {ageThreshold: 'PT2S', tasks: ['a', 'b']};
    system.run(params, "task");
};

exports.vacuumDefault = function () {
    system.run({}, "task");
};
