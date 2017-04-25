var assert = require('/lib/xp/assert');
var i18n = require('/lib/xp/i18n');

exports.testLocalize = function () {
    var result = i18n.localize({
        key: 'myKey'
    });

    assert.assertEquals("[myKey]", result);
};

exports.testLocalizeWithLocale = function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: 'en-US'
    });

    assert.assertEquals("[myKey]", result);
};

exports.testLocalizeWithPlaceholders = function () {
    var result = i18n.localize({
        key: 'myKey',
        values: ['a', 1, 'b']
    });

    assert.assertEquals("[myKey, a, 1, b]", result);
};

exports.testGetPhrases = function () {
    var actual = {
        "a": "1",
        "b": "2"
    };
    var result = i18n.getPhrases();

    assert.assertJsonEquals(actual, result);
};

exports.testExamples = function () {
    testInstance.runScript('/site/lib/xp/examples/i18n/localize.js')
};
