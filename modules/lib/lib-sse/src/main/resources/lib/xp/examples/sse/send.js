const sseLib = require('/lib/xp/sse');

// BEGIN
let clientId = 'client-id-from-sse-event';
sseLib.send({clientId: clientId, message: {event: 'message', data: 'Hello!'}});
// END
