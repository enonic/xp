var assert = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.submitTask = function () {

    var taskId = taskLib.submit({
        description: 'Background task',
        task: function () {
            // do something
        }
    });

    assert.assertEquals('123', taskId);
};

exports.submitTaskThrowingError = function () {

    var taskId = taskLib.submit({
        description: 'Background task',
        task: function () {
            throw new Error("Something went wrong");
        }
    });

    assert.assertEquals('123', taskId);
};
