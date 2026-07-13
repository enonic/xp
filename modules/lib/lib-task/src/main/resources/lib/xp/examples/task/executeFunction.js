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
// Execute a detached task function: runs isolated from the submitting scope (and in parallel on
// pooled script engines). Captured variables are not available - pass data via `params`.
var detachedTaskId = taskLib.executeFunction({
    description: 'Detached background task',
    detached: true,
    params: {count: 42},
    func: function (params) {
        log.info('Processing %s items', params.count);
    }
});
// END

assert.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', detachedTaskId);
