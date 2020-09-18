var t = require('/lib/xp/testing.js');
var libVhost = require('/lib/xp/vhost');

exports.testEnabled = function () {
    const result = libVhost.isEnabled();

    t.assertJsonEquals(true, result);
};

exports.testGetVirtualHosts = function () {
    const result = libVhost.list();

    t.assertEquals(JSON.stringify({
        'vhosts': [{
            'name': 'a',
            'source': '/a',
            'target': '/other/a',
            'host': 'localhost',
            'defaultIdProviderKey': 'default',
            'idProviderKeys': [{'idProviderKey': 'default'}]
        }]
    }), JSON.stringify(result));
};
