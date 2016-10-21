var assert = require('/lib/xp/assert.js');
var taskLib = require('/lib/xp/task.js');

exports.reportProgress = function () {

    var taskId = taskLib.submit({
        description: 'Background task',
        task: function () {

            taskLib.reportProgress({info: 'Starting task'});

            for (var i = 0; i < 10; i++) {
                taskLib.reportProgress({
                    info: 'Step ' + i,
                    current: i,
                    total: 10
                });
            }

            taskLib.reportProgress({info: 'Work completed'});
        }
    });

    assert.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);
};

exports.reportProgressOutsideTask = function () {

    taskLib.reportProgress({info: 'Starting task'});

    assert.assertTrue(false, 'Expected exception');
};