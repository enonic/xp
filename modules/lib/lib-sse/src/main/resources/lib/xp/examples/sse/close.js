const sseLib = require('/lib/xp/sse');

// BEGIN
let clientId = 'client-id-from-sse-event';
sseLib.close({clientId: clientId});
// END
