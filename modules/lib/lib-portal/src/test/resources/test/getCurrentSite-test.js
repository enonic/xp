var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
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
        'state': 'READY',
        'checks': {}
    }
};

exports.currentSite = function () {
    var result = portal.getSite();
    assert.assertJsonEquals(expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSite();
    assert.assertNull(result);
};
