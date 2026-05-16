var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var regions = portalLib.getRegions();
log.info('Number of regions = %s', regions.length);
// END

// BEGIN
// Array of regions returned, in declaration order.
var expected = [
    {
        'components': [
            {
                'path': '/main/0/bottom/0',
                'type': 'part',
                'descriptor': 'myapplication:mypart',
                'config': {
                    'a': '1'
                }
            }
        ],
        'name': 'bottom'
    }
];
// END

assert.assertJsonEquals(expected, regions);
