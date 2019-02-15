var taskLib = require('/lib/xp/task.js');
var assert = require('/lib/xp/testing');

var count = 0;
function fetchRemoteData() {
    count++;
    return count < 2 ? null : [];
}

// BEGIN
var retries = 3;
var result = fetchRemoteData();

while (!result && retries > 0) {
    taskLib.sleep(500); // wait half a second before retrying
    retries--;
    result = fetchRemoteData();
}
// END

assert.assertEquals(2, retries);