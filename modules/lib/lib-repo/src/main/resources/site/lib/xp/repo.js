/**
 * Node repository related functions.
 *
 * @example
 * var repoLib = require('/lib/xp/repo');
 *
 * @module lib/xp/repo
 */

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
    }

    return value;
}

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

/**
 * Creates a repository
 *
 * @example-ref examples/repo/create.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Repository ID.
 * @param {object} [params.settings] Repository settings.
 * @param {object} [params.settings.validationSettings] Validation settings.
 * @param {boolean} [params.settings.validationSettings.checkExists=true] Activate node existence check.
 * @param {boolean} [params.settings.validationSettings.checkParentExists=true] Activate parent node existence check.
 * @param {boolean} [params.settings.validationSettings.checkPermissions=true] Activate permissions check.
 * @param {boolean} [params.settings.indexConfigs] TBD.
 *
 */
exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.repo.CreateRepositoryHandler');
    bean.repositoryId = required(params, 'id');

    var settings = params.settings || {};
    var validationSettings = settings.validationSettings || {};
    bean.checkExists = __.nullOrValue(validationSettings.checkExists);
    bean.checkParentExists = __.nullOrValue(validationSettings.checkParentExists);
    bean.checkPermissions = __.nullOrValue(validationSettings.checkPermissions);

    return __.toNativeObject(bean.execute());
};

/**
 * Retrieves a repository
 *
 * @example-ref examples/repo/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.id Repository ID.
 * @return {object} The repository (as JSON).
 *
 */
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.repo.GetRepositoryHandler');
    bean.repositoryId = required(params, 'id');

    return __.toNativeObject(bean.execute());
};
