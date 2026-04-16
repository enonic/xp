const sseLib = require('/lib/xp/sse');

// BEGIN
sseLib.sendToGroup({group: 'updates', message: {event: 'message', data: 'Notice this message!'}});
// END
