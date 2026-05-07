const sseLib = require('/lib/xp/sse');

// BEGIN
let clientId = 'client-id-from-sse-event';
for (let i = 0; i < 10; i++) {
    if (!sseLib.isOpen({clientId: clientId})) {
        break;
    }
    let data = computeExpensiveData(i);
    sseLib.send({clientId: clientId, message: {event: 'update', data: data}});
}
// END
