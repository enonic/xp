var t = require('/lib/xp/testing.js');
var taskLib = require('/lib/xp/task.js');

exports.getExistingTasks = function () {

    var result = taskLib.list();

    var expectedJson = [
        {
            "description": "Long running task",
            "id": "7ca603c1-3b88-4009-8f30-46ddbcc4bb19",
            "name": "task-7ca603c1-3b88-4009-8f30-46ddbcc4bb19",
            "state": "RUNNING",
            "application": "com.enonic.app1",
            "user": "user:store:user1",
            "startTime": "2017-10-01T09:00:00Z",
            "progress": {
                "info": "Processing item 33",
                "current": 33,
                "total": 42
            }
        },
        {
            "description": "Update statistics",
            "id": "b6173bcb-bf54-409b-aa6b-96ae6fcec263",
            "name": "task-b6173bcb-bf54-409b-aa6b-96ae6fcec263",
            "state": "FINISHED",
            "application": "com.enonic.app2",
            "user": "user:store:user2",
            "startTime": "2017-10-02T09:00:00Z",
            "progress": {
                "info": "Work completed",
                "current": 0,
                "total": 0
            }
        },
        {
            "description": "Import remote data",
            "id": "e1f57280-d672-4cd8-b674-98e26e5b69ae",
            "name": "task-e1f57280-d672-4cd8-b674-98e26e5b69ae",
            "state": "FAILED",
            "application": "com.enonic.app3",
            "user": "user:store:user3",
            "startTime": "2017-10-03T09:00:00Z",
            "progress": {
                "info": "Fetching data",
                "current": 33,
                "total": 100
            }
        }
    ];

    t.assertJsonEquals(expectedJson, result);
};

exports.listNone = function () {

    var result = taskLib.list();

    var expectedJson = [];

    t.assertJsonEquals(expectedJson, result);

};
