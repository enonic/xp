var assert = require('/lib/xp/assert.js');
var i18n = require('/lib/xp/i18n.js');

exports.localize = function () {
    var result = i18n.localize({
        key: 'myKey'
    });

    assert.assertEquals("[myKey]", result);
};

exports.localize_with_locale = function () {
    var result = i18n.localize({
        key: 'myKey',
        locale: 'en-US'
    });

    assert.assertEquals("[myKey]", result);
};

exports.localize_with_placeholders = function () {
    var result = i18n.localize({
        key: 'myKey',
        values: ['a', 1, 'b']
    });

    assert.assertEquals("[myKey, a, 1, b]", result);
};
