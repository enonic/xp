var bean = __.getBean('com.enonic.xp.lib.i18n.LocaleScriptBean');

exports.localize = function (param) {
    return bean.localize(param.key, param.locale, __.toScriptValue(param.values));
};
