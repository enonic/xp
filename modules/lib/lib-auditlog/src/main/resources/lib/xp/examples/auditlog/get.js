var auditLib = require('/lib/xp/auditlog');
var assert = require('/lib/xp/testing');

// BEGIN
// Gets an audit log by id.
var log = auditLib.get({
    id: '90b976f7-55ab-48ef-acb8-e7c6f0744442'
});
// END

var expected = {
    '_id': '90b976f7-55ab-48ef-acb8-e7c6f0744442',
    'type': 'testlog',
    'time': '2019-08-12T08:44:02.767Z',
    'source': 'testbundle',
    'user': 'user:system:anonymous',
    'message': 'Fetched message',
    'objectUris': [],
    'data': {}
};

assert.assertJsonEquals(expected, log);