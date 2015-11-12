/**
 * Internationalization functions.
 *
 * @example
 * var authLib = require('/lib/xp/i18n');
 *
 * @module lib/xp/i18n
 */

exports.localize = function (param) {
    var bean = __.newBean('com.enonic.xp.lib.i18n.LocaleScriptBean');
    return bean.localize(param.key, __.nullOrValue(param.locale), __.toScriptValue(param.values));
};
