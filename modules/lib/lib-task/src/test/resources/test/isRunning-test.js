var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.isRunning = function () {

    var result = taskLib.isRunning("my-task");

    t.assertFalse(result);

};
