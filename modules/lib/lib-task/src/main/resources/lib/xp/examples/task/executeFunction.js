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
