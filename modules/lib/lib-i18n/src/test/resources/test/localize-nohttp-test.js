var t = require('/lib/xp/testing');
var i18n = require('/lib/xp/i18n');

exports.testLocalize = function () {

    var result = i18n.localize({
        key: 'myKey'});

    t.assertEquals('value-1', result);
};
