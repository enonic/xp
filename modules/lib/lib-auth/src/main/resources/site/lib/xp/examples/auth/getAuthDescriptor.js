var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

var principal = authLib.getAuthDescriptor({key: 'myUserStore'});

var expected = {
    key: 'com.enonic.app.test',
    mode: 'LOCAL',
    config: {}
};

t.assertJsonEquals(expected, principal);
