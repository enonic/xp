var assert = require('/lib/xp/assert.js');
var taskLib = require('/lib/xp/task.js');

exports.submitTask = function () {

    var taskId = taskLib.submitNamed('my-task');

    assert.assertEquals('123', taskId);
};

exports.submitTaskFromApp = function () {

    var taskId = taskLib.submitNamed('other-app:some-task');

    assert.assertEquals('123', taskId);
};
