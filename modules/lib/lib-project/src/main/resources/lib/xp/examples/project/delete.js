// BEGIN
// Deletes an existing content project
var projectLib = require('/lib/xp/project');
var result = projectLib.delete({
    id: 'my-project'
});
// END

// BEGIN
// Deletes an existing content project inside context with `system.admin` role
var projectLib2 = require('/lib/xp/project');
var contextLib = require('/lib/xp/context');

var deleteProject = function () {
    return projectLib2.delete({
        id: 'my-project'
    });
};

var result2 = contextLib.run({
    principals: ['role:system.admin']
}, deleteProject);
// END

// BEGIN
// `true` if project was deleted.
var expected = true;
// END
