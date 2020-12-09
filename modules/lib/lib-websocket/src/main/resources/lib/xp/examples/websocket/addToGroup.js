var webSocketLib = require('/lib/xp/websocket');

// BEGIN
webSocketLib.addToGroup('people', session.id);
const size = webSocketLib.getGroupSize('people');
// END
