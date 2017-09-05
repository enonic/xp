var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Execute task and keep taskId for polling status
var taskId = taskLib.submit({
    description: 'Background task',
    task: function () {
        longRunningTask();
    }
});
// END

assert.assertEquals("7ca603c1-3b88-4009-8f30-46ddbcc4bb19", taskId);
