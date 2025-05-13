var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.executeFunction = function () {

    var taskId = taskLib.executeFunction({
        description: 'Background task',
        func() {
            // do something
        }
    });

    t.assertEquals('123', taskId);
};

exports.executeFunctionThrowingError = function () {

    var taskId = taskLib.executeFunction({
        description: 'Background task',
        func() {
            throw new Error('Something went wrong');
        }
    });

    t.assertEquals('123', taskId);
};
