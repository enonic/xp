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

exports.executeFunctionUsingLibs = function () {

    taskLib.executeFunction({
        description: 'Task using libs',
        params: {a: 40, b: 2},
        func: function (params) {
            // a detached function can load libraries and log: the runner provides
            // `log`, `require`, `resolve` and `__`
            var testing = require('/lib/xp/testing.js');
            var sum = params.a + params.b;
            testing.assertNotNull(sum);
            log.info('Task computed %s', sum);
            testInstance.record(sum);
        }
    });
};

exports.executeArrayParams = function () {

    taskLib.executeFunction({
        description: 'Array params',
        params: [20, 22],
        func: function (params) {
            testInstance.record(params[0] + params[1]);
        }
    });
};

exports.executeScalarParams = function () {

    taskLib.executeFunction({
        description: 'Scalar params',
        params: 42,
        func: function (params) {
            testInstance.record(params);
        }
    });
};

exports.executeRejectsFunctionParams = function () {

    taskLib.executeFunction({
        description: 'Bad params',
        params: {
            cb: function () {
            }
        },
        func: function () {
        }
    });
};

exports.executeRejectsFunctionInArrayParams = function () {

    taskLib.executeFunction({
        description: 'Bad array params',
        params: {
            list: [function () {
            }]
        },
        func: function () {
        }
    });
};

exports.executeRejectsFunctionAsParams = function () {

    taskLib.executeFunction({
        description: 'Function as params',
        params: function () {
        },
        func: function () {
        }
    });
};
