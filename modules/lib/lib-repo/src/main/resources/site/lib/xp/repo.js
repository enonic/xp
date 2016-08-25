/**
 * Node repository related functions.
 *
 * @example
 * var repoLib = require('/lib/xp/repo');
 *
 * @module lib/xp/repo
 */

/**
 * Refresh the data for the given index-type in the current repository.
 *
 * @example-ref examples/repo/refresh.js
 *
 * @param {string} [mode='all'] Index type to be refreshed. Possible values: 'all' | 'search' | 'storage'.
 *
 */
exports.refresh = function (mode) {

    var bean = __.newBean('com.enonic.xp.lib.repo.RefreshHandler');

    bean.mode = __.nullOrValue(mode);

    bean.refresh();
};
