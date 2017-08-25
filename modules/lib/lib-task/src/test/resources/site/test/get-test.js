var assert = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.getExistingTask = function () {

    var result = taskLib.get("123");

    var expectedJson = {
        "description": "Long running task",
        "id": "123",
        "state": "RUNNING",
        "progress": {
            "info": "Processing item 33",
            "current": 33,
            "total": 42
        }
    };
    assert.assertJsonEquals(expectedJson, result);
};

exports.getTaskNotFound = function () {

    var result = taskLib.get("123");

    assert.assertNull(result);

};
