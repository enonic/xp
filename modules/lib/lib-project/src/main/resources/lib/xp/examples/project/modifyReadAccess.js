// BEGIN
// Toggles public/private READ access on an existing content project.
var projectLib = require('/lib/xp/project');

var currentPermissions = projectLib.addPermissions({
    id: 'my-project',
    readAccess: {
        public: false
    }
});
// END

// BEGIN
var expected = {
    'id': 'my-project',
    'readAccess': {
        'public': false
    }
};
// END

