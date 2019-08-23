var auditLib = require('/lib/xp/auditlog');
var assert = require('/lib/xp/testing');

// BEGIN
// Creates an audit log.
var log1 = auditLib.log({
    type: 'testlog'
});
// END

// BEGIN
// Creates an audit log with more custom parameters.
var log2 = auditLib.log({
    type: 'testlog',
    time: '2019-08-12T08:44:02.767Z',
    source: 'unittests',
    user: 'user:system:anonymous',
    message: 'Audit log message',
    objects: [
        'some:resource:uri'
    ],
    data: {
        'custom': 'string',
        'somevalue': 2.5
    }
});
// END

var expected1 = {
    '_id': '90b976f7-55ab-48ef-acb8-e7c6f0744442',
    'type': 'testlog',
    'time': '2019-08-12T08:44:02.767Z',
    'source': 'testbundle',
    'user': 'user:system:anonymous',
    'message': '',
    'objects': [],
    'data': {}
};

assert.assertJsonEquals(expected1, log1);

var expected2 = {
    '_id': '90b976f7-55ab-48ef-acb8-e7c6f0744442',
    'type': 'testlog',
    'time': '2019-08-12T08:44:02.767Z',
    'source': 'testbundle',
    'user': 'user:system:anonymous',
    'message': 'Audit log message',
    'objects': [
        'some:resource:uri'
    ],
    'data': {
        'custom': 'string',
        'somevalue': 2.5
    }
};

assert.assertJsonEquals(expected2, log2);