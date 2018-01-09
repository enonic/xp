var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.getAuthDescriptor = function () {

    var result = auth.getAuthDescriptor({key: 'myUserStore'});

    var expected = {
        key: 'com.enonic.app.test',
        mode: 'LOCAL',
        config: {}
    };

    t.assertJsonEquals(expected, result);

};

exports.getNonExistingAuthDescriptor = function () {

    var result = auth.getAuthDescriptor({key: 'myUserStore'});

    t.assertEquals(null, result);

};
