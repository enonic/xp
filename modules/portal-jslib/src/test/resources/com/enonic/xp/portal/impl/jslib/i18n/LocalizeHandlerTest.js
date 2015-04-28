var localize_with_locale_expected = "result with locale";
var localize_without_locale_expected = "result without locale";
var localize_without_params_expected = "result without params";

exports.localize_with_locale = function () {
    var result = execute('i18n.localize', {
        key: 'key',
        locale: 'en',
        params: [1, 'a']
    });
    assert.assertEquals(localize_with_locale_expected, result);
};


exports.localize_without_locale = function () {
    var result = execute('i18n.localize', {
        key: 'key',
        params: [1, 'a']
    });
    assert.assertEquals(localize_without_locale_expected, result);
};

exports.localize_without_params = function () {
    var result = execute('i18n.localize', {
        key: 'key'
    });
    assert.assertEquals(localize_without_params_expected, result);

};
