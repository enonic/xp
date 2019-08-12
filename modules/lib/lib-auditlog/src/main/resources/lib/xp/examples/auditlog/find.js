var auditLib = require('/lib/xp/auditlog');
var assert = require('/lib/xp/testing');

// BEGIN
// Find audit logs by ids
var result = auditLib.find({
    ids: [
        '90b976f7-55ab-48ef-acb8-e7c6f0744442',
        '00c4e51d-ee39-4f0e-9075-5af00b5830c4' // Log that does not exist
    ]
});
// END

var expected = {
    'total': 1,
    'hits': [
        {
            '_id': '90b976f7-55ab-48ef-acb8-e7c6f0744442',
            'type': 'testlog',
            'time': '2019-08-12T08:44:02.767Z',
            'source': 'testbundle',
            'user': 'user:system:anonymous',
            'message': 'Fetched message',
            'objectUris': [],
            'data': {}
        }
    ]
};

assert.assertJsonEquals(expected, result);