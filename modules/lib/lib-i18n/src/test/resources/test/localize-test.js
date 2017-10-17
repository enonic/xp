var t = require('/lib/xp/testing');
var i18n = require('/lib/xp/i18n');

exports.testLocalize = function () {
    var result = i18n.localize({
        key: 'myKey'
    });

    t.assertEquals("[myKey]", result);
};

exports.testLocalizeWithLocale = function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: 'en_US'
    });

    t.assertEquals("[myKey]", result);
};

exports.testLocalizeWithMultipleLocale = function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: ['es', 'en_US']
    });

    t.assertEquals("[myKey]", result);
};

exports.testLcalizeWithPlaceholders = function () {
    var result = i18n.localize({
        key: 'myKey',
        values: ['a', 1, 'b']
    });

    t.assertEquals("[myKey, a, 1, b]", result);
};

exports.testGetPhrases = function () {
    var actual = {
        "a": "1",
        "b": "2"
    };

    var result = i18n.getPhrases('en', null);
    t.assertJsonEquals(actual, result);
};

exports.testExamples = function () {
    t.runScript('/site/lib/xp/examples/i18n/localize.js');
};
