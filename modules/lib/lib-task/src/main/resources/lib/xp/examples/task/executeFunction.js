var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Execute task function and keep taskId for polling status
var taskId = taskLib.executeFunction({
    description: 'Background task',
    func() {
        longRunningTask();
    }
});
// END

assert.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);

// BEGIN
// Pass data to the task function via `params`. On pooled script engines the function runs
// detached from the submitting scope (and in parallel): captured variables are not available
// there - `params`, `log` and `require` are.
var paramsTaskId = taskLib.executeFunction({
    description: 'Background task with params',
    params: {count: 42},
    func: function (params) {
        log.info('Processing %s items', params.count);
    }
});
// END

assert.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', paramsTaskId);
