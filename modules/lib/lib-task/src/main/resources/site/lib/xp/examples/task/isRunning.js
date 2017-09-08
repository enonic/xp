var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Check if a task is currently running
var isRunning = taskLib.isRunning('com.enonic.myapp:clean-up-task');

if (!isRunning) {
    log.info('Start task...');
} else {
    log.info('Task already running');
}
// END

assert.assertTrue(isRunning);
