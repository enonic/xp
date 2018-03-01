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


var adminToolsUriPrefix = '/admin/tool';

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
 * @returns {object} JSON object with phrases.
 */
exports.getPhrases = function () {
    return JSON.stringify(i18n.getPhrases(exports.getLocales(), ['i18n/common', 'i18n/phrases']));
};

/**
 * Returns the URL for launcher panel.
 *
 * @returns {string} URL.
 */
exports.getLauncherUrl = function () {
    return helper.getLauncherToolUrl();
};

/**
 * Returns the URL for launcher javascript.
 *
 * @returns {string} Path.
 */
exports.getLauncherPath = function () {
    return portal.assetUrl({
        application: helper.getHomeAppName(),
        path: '/js/launcher/bundle.js'
    });
};

/**
 * Returns the URL for an admin tool of specific application.
 * @param {string} application Full application name (f.ex, 'com.enonic.app')
 * @param {string} tool Name of the tool inside an app (f.ex, 'main')
 *
 * @returns {string} URL.
 */
exports.getToolUrl = function (application, tool) {
    if (application) {
        return helper.generateAdminToolUri(application, tool);
    }

    return helper.generateHomeToolUri();
};

/**
 * Returns the URL for the Home admin tool.
 * @param {object} [params] Parameter object
 * @param {string} [params.type=server] URL type. Either `server` (server-relative URL) or `absolute`.
 *
 * @returns {string} URL.
 */
exports.getHomeToolUrl = function (params) {
    return portal.url({
        path: adminToolsUriPrefix,
        type: params && params.type
    });
};

/**
 * Returns installation name.
 *
 * @returns {string} Installation name.
 */
exports.getInstallation = function () {
    return helper.getInstallation();
};

/**
 * Returns version of XP installation.
 *
 * @returns {string} Version.
 */
exports.getVersion = function () {
    return helper.getVersion();
};
