exports.localize_with_locale = function () {
    var result = execute('i18n.localize', {
        key: 'myKey',
        locale: 'en-US',
        a: 1,
        b: 'test'
    });
    assert.assertEquals("[myKey]", result);
};


exports.localize_without_locale = function () {
    var result = execute('i18n.localize', {
        key: 'myKey',
        a: 1,
        b: 'test'
    });
    assert.assertEquals("[myKey]", result);
};

exports.localize_without_params = function () {
    var result = execute('i18n.localize', {
        key: 'myKey'
    });
    assert.assertEquals("[myKey]", result);

};

exports.localize_with_placeholders = function () {
    var result = execute('i18n.localize', {
        key: 'myKey',
        values: ['a', 1, 'b']
    });
    assert.assertEquals("[myKey, a, 1, b]", result);

};