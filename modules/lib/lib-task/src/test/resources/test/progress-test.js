var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.reportProgress = function () {

    var taskId = taskLib.executeFunction({
        description: 'Background task',
        func() {

            taskLib.progress({info: 'Starting task'});

            for (var i = 0; i < 10; i++) {
                taskLib.progress({
                    info: 'Step ' + i,
                    current: i,
                    total: 10
                });
            }

            taskLib.progress({info: 'Work completed'});
        }
    });

    t.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);
};

exports.reportProgressOutsideTask = function () {

    taskLib.progress({info: 'Starting task'});

    t.assertTrue(false, 'Expected exception');
};

exports.reportProgressWithoutInfo = function () {

    var taskId = taskLib.executeFunction({
        description: 'Background task',
        func() {

            for (var i = 0; i < 10; i++) {
                taskLib.progress({
                    current: i,
                    total: 10
                });
            }

        }
    });

    t.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);
};

exports.reportProgressInfoOnly = function () {

    var taskId = taskLib.executeFunction({
        description: 'Background task',
        func() {

            taskLib.progress({info: 'Step 1'});
            taskLib.progress({info: 'Step 2'});
            taskLib.progress({info: 'Step 3'});

        }
    });

    t.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);
};
