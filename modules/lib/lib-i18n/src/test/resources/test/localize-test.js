var t = require('/lib/xp/testing');
var i18n = require('/lib/xp/i18n');

exports.testLocalize = function () {
    var result = i18n.localize({
        key: 'myKey'
    });

    t.assertEquals('[myKey]', result);
};

exports.testLocalizeWithLocale = function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: 'en_US'
    });

    t.assertEquals('[myKey]', result);
};

exports.testLocalizeWithMultipleLocale = function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: ['es', 'en_US']
    });

    t.assertEquals('[myKey]', result);
};

exports.testLocalizeWithPlaceholders = function () {
    var result = i18n.localize({
        key: 'myKey',
        values: ['a', 1, 'b']
    });

    t.assertEquals('[myKey, a, 1, b]', result);
};

exports.testLocalizeNotLocalized = function () {
    var result = i18n.localize({
        key: 'notLocalized',
    });

    t.assertEquals('NOT_TRANSLATED', result);
}

exports.testLocalizeNotLocalizedCustom = function () {
    var result = i18n.localize({
        key: 'notLocalized',
        fallbackMessage: 'fallback'
    });

    t.assertEquals('fallback', result);
}

exports.testGetPhrases = function () {
    var actual = {
        'a': '1',
        'b': '2'
    };

    var result = i18n.getPhrases('en');
    t.assertJsonEquals(actual, result);
};

exports.testExamples = function () {
    t.runScript('/lib/xp/examples/i18n/localize.js');
};
