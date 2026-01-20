const assert = require('/lib/xp/testing');
const projectLib = require('/lib/xp/project');
const contextLib = require('/lib/xp/context');

// BEGIN
// Deletes an existing content project
var result = projectLib.deleteProject({
    id: 'my-project'
});
// END

// BEGIN
// Deletes an existing content project inside context with `system.admin` role
var deleteProject = function () {
    return projectLib.deleteProject({
        id: 'my-project'
    });
};

var result2 = contextLib.run({
    principals: ['role:system.admin']
}, deleteProject);
// END

assert.assertTrue(result2);
