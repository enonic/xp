var taskLib = require('/lib/xp/task.js');
var t = require('/lib/xp/testing');

// BEGIN
// Execute task, located in the current app, by name
var taskId = taskLib.submitTask({
    descriptor: 'job42',
    config: {
        count: 123
    }
});
// END

t.assertEquals('7ca603c1-3b88-4009-8f30-46ddbcc4bb19', taskId);

// BEGIN
// Execute a task located in a different app
taskId = taskLib.submitTask({
    descriptor: 'com.enonic.app.myapp:work',
    config: {}
});
// END

t.assertNotNull(taskId);
