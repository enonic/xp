const sseLib = require('/lib/xp/sse');

// BEGIN
let clientId = 'client-id-from-sse-event';
sseLib.addToGroup({group: 'updates', clientId: clientId});
let size = sseLib.getGroupSize({group: 'updates'});
// END
