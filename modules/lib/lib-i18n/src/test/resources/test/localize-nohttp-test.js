var t = require('/lib/xp/testing');
var i18n = require('/lib/xp/i18n');

exports.testLocalize = function () {
    testInstance.removeRequest();

    var result = i18n.localize({
        key: 'myKey',
        application: 'com.enonic.myapplication'
    });

    t.assertEquals("value-1", result);
};
