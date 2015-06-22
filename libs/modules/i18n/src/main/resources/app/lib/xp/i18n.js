exports.localize = function (param) {
    var bean = __.newBean('com.enonic.xp.lib.i18n.LocaleScriptBean');
    return bean.localize(param.key, __.nullOrValue(param.locale), __.toScriptValue(param.values));
};
