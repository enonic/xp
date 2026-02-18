var sseLib = require('/lib/xp/sse');

// BEGIN
sseLib.addToGroup('updates', session.id);
var size = sseLib.getGroupSize('updates');
// END
