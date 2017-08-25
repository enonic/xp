var t = require('/lib/xp/testkit');
var i18n = require('/lib/xp/i18n');

t.test('localize', function () {
    var result = i18n.localize({
        key: 'myKey'
    });

    t.assertEquals("[myKey]", result);
});

t.test('localize with locale', function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: 'en-US'
    });

    t.assertEquals("[myKey]", result);
});

t.test('localize with placeholders', function () {
    var result = i18n.localize({
        key: 'myKey',
        values: ['a', 1, 'b']
    });

    t.assertEquals("[myKey, a, 1, b]", result);
});

t.test('get phrases', function () {
    var actual = {
        "a": "1",
        "b": "2"
    };

    var result = i18n.getPhrases('en', null);
    t.assertJson(actual, result);
});
