/**
 * Admin related functions.
 *
 * @example
 * var adminLib = require('/lib/xp/admin');
 *
 * @module admin
 */

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
 * Returns the current locale based on request or server.
 *
 * @returns {string} Current locale.
 */
exports.getLocale = function () {
    return helper.getLocale();
};

