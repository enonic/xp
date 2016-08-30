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
 * @param {object} params JSON with the parameters.
 * @param {string} [params.mode='all'] Index type to be refreshed. Possible values: 'all' | 'search' | 'storage'.
 * @param {string} [params.repo='cms-repo'] Repository id: 'cms-repo' | 'system-repo'. Default is the current repository set in portal.
 *
 */
exports.refresh = function (params) {

    var bean = __.newBean('com.enonic.xp.lib.repo.RefreshHandler');
    params = params || {};
    bean.mode = __.nullOrValue(params.mode);
    bean.repoId = __.nullOrValue(params.repo);

    bean.refresh();
};
