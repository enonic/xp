// BEGIN
// Fetches an existing content project
var projectLib = require('/lib/xp/project');
var project = projectLib.getAvailableApplications({
    id: 'my-project'
});
// END

// BEGIN
var expected = ['app1', 'app2'];
// END
