/**
 * Application related functions.
 *
 * @example
 * var schema = require('/lib/xp/app');
 *
 * @module app
 */

/* global __*/

function required(params, name) {
    var value = params[String(name)];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

/**
 * Creates virtual application.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {object} created application.
 */
exports.createVirtualApplication = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.app.CreateVirtualApplicationHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};

/**
 * Deletes virtual application.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {boolean} deletion result.
 */
exports.deleteVirtualApplication = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.app.DeleteVirtualApplicationHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};

/**
 * Fetches application by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {object} fetched application.
 */
exports.get = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.app.GetApplicationHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};

/**
 * Fetches both static and virtual applications.
 *
 * @returns {object[]} applications list.
 */
exports.list = function () {
    const bean = __.newBean('com.enonic.xp.lib.app.ListApplicationsHandler');
    return __.toNativeObject(bean.execute());
};

/**
 * Fetches application descriptor by key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {object} fetched application descriptor.
 */
exports.getDescriptor = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.app.GetApplicationDescriptorHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};

/**
 * Checks if there is a virtual app with the app key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {boolean} result.
 */
exports.hasVirtual = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.app.HasVirtualApplicationHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};

/**
 * Checks if there is a real app with the app key.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {boolean} result.
 */
exports.hasReal = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.app.HasRealApplicationHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};




