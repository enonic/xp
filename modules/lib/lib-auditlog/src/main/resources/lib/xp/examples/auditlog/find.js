var auditLib = require('/lib/xp/auditlog');
var assert = require('/lib/xp/testing');

// BEGIN
// Find first audit log by ids
var result = auditLib.find({
    start: 0,
    count: 1,
    ids: [
        '90b976f7-55ab-48ef-acb8-e7c6f0744442',
        '00c4e51d-ee39-4f0e-9075-5af00b5830c4'
    ]
});
// END

var expected = {
    'total': 2,
    'count': 1,
    'hits': [
        {
            '_id': '90b976f7-55ab-48ef-acb8-e7c6f0744442',
            'type': 'testlog',
            'time': '2019-08-12T08:44:02.767Z',
            'source': 'testbundle',
            'user': 'user:system:anonymous',
            'objects': [],
            'data': {}
        }
    ]
};

assert.assertJsonEquals(expected, result);
