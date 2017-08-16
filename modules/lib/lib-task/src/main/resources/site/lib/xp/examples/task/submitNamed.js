var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/assert');

// BEGIN
// Execute task, located in the current app, by name
var taskId = taskLib.submitNamed('job42');
// END

assert.assertEquals("7ca603c1-3b88-4009-8f30-46ddbcc4bb19", taskId);

// BEGIN
// Execute a task located in a different app
var taskId = taskLib.submitNamed('com.enonic.app.myapp:work');
// END
