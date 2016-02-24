var webSocketLib = require('/lib/xp/websocket');

// BEGIN
webSocketLib.send(session.id, 'You said - ' + message);
// END
