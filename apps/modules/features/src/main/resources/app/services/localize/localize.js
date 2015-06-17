function localize(key, locale, params) {

    var test = execute('i18n.localize', {
        key: key,
        locale: locale,
        params: params
    });
}

exports.localize = localize;
