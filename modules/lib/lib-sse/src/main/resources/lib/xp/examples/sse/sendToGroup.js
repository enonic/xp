var sseLib = require('/lib/xp/sse');

// BEGIN
sseLib.sendToGroup({group: 'updates', event: 'message', data: 'Notice this message!'});
// END
