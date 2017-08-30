var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.submitTask = function () {

    var taskId = taskLib.submit({
        description: 'Background task',
        task: function () {
            // do something
        }
    });

    t.assertEquals('123', taskId);
};

exports.submitTaskThrowingError = function () {

    var taskId = taskLib.submit({
        description: 'Background task',
        task: function () {
            throw new Error("Something went wrong");
        }
    });

    t.assertEquals('123', taskId);
};
