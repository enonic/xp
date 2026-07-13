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

exports.executeFunctionWithParams = function () {

    var taskId = taskLib.executeFunction({
        description: 'Task with params',
        params: {a: 20, b: 22},
        func: function (params) {
            testInstance.record(params.a + params.b);
        }
    });

    t.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);
};

exports.executeClosureFunction = function () {

    var captured = 'closure';

    taskLib.executeFunction({
        description: 'Closure task',
        func: function () {
            testInstance.record(captured);
        }
    });
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

exports.executeDetachedArrayParams = function () {

    taskLib.executeFunction({
        description: 'Array params',
        detached: true,
        params: [20, 22],
        func: function (params) {
            testInstance.record(params[0] + params[1]);
        }
    });
};

exports.executeDetachedScalarParams = function () {

    taskLib.executeFunction({
        description: 'Scalar params',
        detached: true,
        params: 42,
        func: function (params) {
            testInstance.record(params);
        }
    });
};

exports.executeDetachedRejectsFunctionInArrayParams = function () {

    taskLib.executeFunction({
        description: 'Bad array params',
        detached: true,
        params: {
            list: [function () {
            }]
        },
        func: function () {
        }
    });
};

exports.executeDetachedRejectsFunctionAsParams = function () {

    taskLib.executeFunction({
        description: 'Function as params',
        detached: true,
        params: function () {
        },
        func: function () {
        }
    });
};
