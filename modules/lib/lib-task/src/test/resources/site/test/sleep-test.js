var assert = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.sleep = function () {

    var System = Java.type("java.lang.System");
    var t1 = System.currentTimeMillis();

    taskLib.sleep(200);

    var t2 = System.currentTimeMillis();

    assert.assertTrue(t2 - t1 >= 200);
};
