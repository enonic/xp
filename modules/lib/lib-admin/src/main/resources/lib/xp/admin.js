/**
 * Admin related functions.
 *
 * @example
 * var adminLib = require('/lib/xp/admin');
 *
 * @module admin
 */

var i18n = require('/lib/xp/i18n');
var portal = require('/lib/xp/portal');
var helper = __.newBean('com.enonic.xp.lib.admin.AdminLibHelper');

/**
 * Returns the admin base uri.
 *
 * @returns {string} Admin base uri.
 */
exports.getBaseUri = function () {
    return helper.getBaseUri();
};

/**
 * Returns the admin assets uri.
 *
 * @returns {string} Assets uri.
 */
exports.getAssetsUri = function () {
    return helper.getAssetsUri();
};

/**
 * Returns the preferred locale based on the current HTTP request, or the server default locale if none is specified.
 *
 * @returns {string} Current locale.
 */
exports.getLocale = function () {
    return helper.getLocale();
};

/**
 * Returns the list of preferred locales based on the current HTTP request, or the server default locale if none is specified.
 *
 * @returns {string[]} Current locales in order of preference.
 */
exports.getLocales = function () {
    return __.toNativeObject(helper.getLocales());
};

/**
 * Returns all i18n phrases.
 *
 * @returns {string} Current locale.
 */
exports.getPhrases = function () {
    return JSON.stringify(i18n.getPhrases(exports.getLocales(), ['admin/i18n/common', 'admin/i18n/phrases']));
};

function getMainAppUrl(path) {
    return portal.assetUrl({
        path: path,
        application: 'com.enonic.xp.app.main'
    });
}

/**
 * Return url for the main app.
 *
 * @param path Path for resource.
 */
exports.getMainAppUrl = getMainAppUrl;

/**
 * Returns the URL for launcher javascript.
 */
exports.getLauncherUrl = function () {
    return getMainAppUrl('/js/launcher/bundle.js');
};
