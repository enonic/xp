var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

var count = 0;

function processItem() {
    count++;
}

// BEGIN
// Report progress from inside a running task.
taskLib.progress({info: 'Initializing task'});

for (var i = 0; i < 10; i++) {
    taskLib.progress({
        info: 'Processing item ' + (i + 1),
        current: i,
        total: 10
    });

    processItem();
}

taskLib.progress({info: 'Task completed'});
// END

assert.assertEquals(10, count);
