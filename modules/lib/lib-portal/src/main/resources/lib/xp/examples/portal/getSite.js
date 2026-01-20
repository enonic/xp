var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var result = portalLib.getSite();
log.info('Current site name = %s', result._name);
// END

// BEGIN
// Site data returned.
var expected = {
    '_id': '100123',
    '_name': 'my-content',
    '_path': '/my-content',
    'type': 'portal:site',
    'valid': false,
    'data': {
        'siteConfig': {
            'applicationKey': 'myapplication',
            'config': {
                'Field': 42
            }
        }
    },
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};
// END

assert.assertJsonEquals(expected, result);
