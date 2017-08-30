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
        "state": "RUNNING",
        "progress": {
            "info": "Processing item 33",
            "current": 33,
            "total": 42
        }
    },
    {
        "description": "Update statistics",
        "id": "b6173bcb-bf54-409b-aa6b-96ae6fcec263",
        "state": "FINISHED",
        "progress": {
            "info": "Work completed",
            "current": 0,
            "total": 0
        }
    },
    {
        "description": "Import remote data",
        "id": "e1f57280-d672-4cd8-b674-98e26e5b69ae",
        "state": "FAILED",
        "progress": {
            "info": "Fetching data",
            "current": 33,
            "total": 100
        }
    }
];
// END

t.assertJsonEquals(expected, tasks);
