var sseLib = require('/lib/xp/sse');

// BEGIN
sseLib.send({id: session.id, event: 'message', data: 'Hello!'});
// END
