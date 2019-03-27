var taskLib = require('/lib/xp/task.js');
var t = require('/lib/xp/testing');

// BEGIN
// Obtains list of active tasks
var tasks = taskLib.list();
// END

// BEGIN
// Tasks returned
var expected = [
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
// END

t.assertJsonEquals(expected, tasks);

// BEGIN
// Obtains list of active tasks with a given name and state
var tasks = taskLib.list({
    name: "com.enonic.myapp:clean-up",
    state: "RUNNING"
});
// END

// BEGIN
// Tasks returned
var expected = [
    {
        "description": "Long running task",
        "id": "7ca603c1-3b88-4009-8f30-46ddbcc4bb19",
        "name": "com.enonic.myapp:clean-up",
        "state": "RUNNING",
        "application": "com.enonic.myapp",
        "user": "user:store:user",
        "startTime": "2017-10-01T09:00:00Z",
        "progress": {
            "info": "Processing item 33",
            "current": 33,
            "total": 42
        }
    }
];
// END

t.assertJsonEquals(expected, tasks);
