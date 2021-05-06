var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.submitTask = function () {

    var taskId = taskLib.submitTask({
        descriptor: 'my-task',
        config: {
            values: ['one', 'two', 'three']
        }
    });

    t.assertEquals('123', taskId);
};

exports.submitTaskFromApp = function () {

    var taskId = taskLib.submitTask({
        descriptor: 'other-app:some-task'
    });

    t.assertEquals('123', taskId);
};
