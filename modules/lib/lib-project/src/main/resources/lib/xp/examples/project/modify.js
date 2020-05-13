// BEGIN
// Modifies an existing content project
var projectLib = require('/lib/xp/project');

var project = projectLib.modify({
    id: 'my-project',
    displayName: 'New project name',
    description: 'New project description',
    language: 'en'
});
// END

// BEGIN
// Modifies an existing content project inside context with `system.admin` role
var projectLib = require('/lib/xp/project');
var contextLib = require('/lib/xp/context');

var modifyProject = function() {
    return projectLib.modify({
        id: 'my-project',
        displayName: 'New project name',
        description: 'New project description',
        language: 'en'
    });
}

var project = contextLib.run({
    principals: ['role:system.admin']
}, modifyProject);
// END

// BEGIN
var expected = {
    'id': 'my-project',
    'displayName': 'New project name',
    'description': 'New project description',
    'language': 'en',
    'permissions': {},
    'readAccess': {
        'public': true
    }
};
// END

