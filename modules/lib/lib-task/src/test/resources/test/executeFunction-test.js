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

exports.executeDetachedFunction = function () {

    var taskId = taskLib.executeFunction({
        description: 'Detached task',
        detached: true,
        params: {a: 1, b: 2},
        func: function (params) {
            // no closures here: only `params` and true globals are in scope
            testInstance.record(params.a + params.b);
        }
    });

    t.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);
};

exports.executeDetachedClosureFails = function () {

    var captured = 42;

    taskLib.executeFunction({
        description: 'Detached closure',
        detached: true,
        func: function () {
            return captured;
        }
    });
};

exports.executeDetachedRejectsFunctionParams = function () {

    taskLib.executeFunction({
        description: 'Bad params',
        detached: true,
        params: {
            cb: function () {
            }
        },
        func: function () {
        }
    });
};
