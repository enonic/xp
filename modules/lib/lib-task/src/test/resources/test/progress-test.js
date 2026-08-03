var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.reportProgress = function () {

    taskLib.progress({info: 'Starting task'});

    for (var i = 0; i < 10; i++) {
        taskLib.progress({
            info: 'Step ' + i,
            current: i,
            total: 10
        });
    }

    taskLib.progress({info: 'Work completed'});
};

exports.reportProgressOutsideTask = function () {

    taskLib.progress({info: 'Starting task'});

    t.assertTrue(false, 'Expected exception');
};

exports.reportProgressWithoutInfo = function () {

    for (var i = 0; i < 10; i++) {
        taskLib.progress({
            current: i,
            total: 10
        });
    }
};

exports.reportProgressInfoOnly = function () {

    taskLib.progress({info: 'Step 1'});
    taskLib.progress({info: 'Step 2'});
    taskLib.progress({info: 'Step 3'});
};
