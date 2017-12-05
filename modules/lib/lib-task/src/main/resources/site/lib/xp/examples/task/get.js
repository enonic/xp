var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

// BEGIN
// Obtains details for an active task
var taskInfo = taskLib.get('7ca603c1-3b88-4009-8f30-46ddbcc4bb19');

if (taskInfo) {
    log.info('Current task state = %s', taskInfo.state);
} else {
    log.info('Task not found');
}
// END

// BEGIN
// Task information returned
var expected = {
    "description": "Long running task",
    "id": "7ca603c1-3b88-4009-8f30-46ddbcc4bb19",
    "name": "task-7ca603c1-3b88-4009-8f30-46ddbcc4bb19",
    "state": "RUNNING",
    "application": "com.enonic.myapp",
    "user": "user:store:me",
    "startTime": "2017-10-01T09:00:00Z",
    "progress": {
        "info": "Processing item 33",
        "current": 33,
        "total": 42
    }
};
// END

assert.assertJsonEquals(expected, taskInfo);
