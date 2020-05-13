// BEGIN
// Returns the list of content projects
var projectLib = require('/lib/xp/project');

var projects = projectLib.list();
// END

// BEGIN
var expected = [
    {
        'id': 'default',
        'displayName': 'Default',
        'description': 'Default project'
    },
    {
        'id': 'my-project',
        'displayName': 'My Content Project',
        'permissions': [],
        'readAccess': {
            'public': true
        }
    }
];
// END

