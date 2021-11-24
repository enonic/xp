/**
 * Internationalization functions.
 *
 * @example
 * var i18nLib = require('/lib/xp/i18n');
 *
 * @module i18n
 */

/* global __ */

/**
 * This function localizes a phrase.
 *
 * @example-ref examples/i18n/localize.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key The property key.
 * @param {string|string[]} [params.locale] A string-representation of a locale, or an array of locales in preferred order. If the locale is not set, the site language is used.
 * @param {string[]} [params.values] Optional placeholder values.
 * @param {string[]} [params.bundles] Optional list of bundle names.
 * @param {string} [params.application] Application key where to find resource bundles. Defaults to current application.
 *
 * @returns {string} The localized string.
 */
exports.localize = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.i18n.LocaleScriptBean');
    params = params || {};
    params.locale = [].concat(params.locale || []);
    bean.setApplication(__.nullOrValue(params.application));
    return bean.localize(params.key, params.locale, __.toScriptValue(params.values), __.nullOrValue(params.bundles));
};

/**
 * This function returns all phrases for the given locale and bundles.
 *
 * @param {string|string[]} locale A string-representation of a locale, or an array of locales in preferred order.
 * @param {string[]} bundles List of bundle names.  Bundle names are specified as paths, relative to the `src/main/resources` folder.
 *
 * @returns {object} An object of all phrases.
 *
 * @example
 * i18nLib.getPhrases('en', ['i18n/phrases'])
 */
exports.getPhrases = function (locale, bundles) {
    var bean = __.newBean('com.enonic.xp.lib.i18n.LocaleScriptBean');
    locale = [].concat(locale || []);
    return __.toNativeObject(bean.getPhrases(locale, bundles));
};

/**
 * This function returns the list of supported locale codes for the specified bundles.
 *
 * @param {string[]} bundles List of bundle names.
 *
 * @returns {string[]} A list of supported locale codes for the specified bundles.
 */
exports.getSupportedLocales = function (bundles) {
    var bean = __.newBean('com.enonic.xp.lib.i18n.LocaleScriptBean');
    return __.toNativeObject(bean.getSupportedLocales(bundles));
};
