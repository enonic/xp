const sseLib = require('/lib/xp/sse');

// BEGIN
let clientId = 'client-id-from-sse-event';
sseLib.removeFromGroup({group: 'updates', clientId: clientId});
// END
