// BEGIN
// Toggles public/private READ access on an existing content project.
var projectLib = require('/lib/xp/project');

var publicRead = projectLib.setPublicRead({
    id: 'my-project',
    publicRead: false
});
// END

// BEGIN
var expected = false;
// END
