var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

var count = 0;
function processItem() {
    count++;
}

// BEGIN
// Execute task and keep taskId for polling status
var taskId = taskLib.submit({
    description: 'Background task',
    task: function () {

        taskLib.progress({info: 'Initializing task'});

        for (var i = 0; i < 10; i++) {
            taskLib.progress({
                info: 'Processing item ' + (i + 1),
                current: i,
                total: 10
            });

            processItem(i);
        }

        taskLib.progress({info: 'Task completed'});
    }
});
// END

assert.assertEquals("7ca603c1-3b88-4009-8f30-46ddbcc4bb19", taskId);
assert.assertEquals(10, count);
