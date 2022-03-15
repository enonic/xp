/**
 * Dynamic schema related functions.
 *
 * @example
 * var schema = require('/lib/xp/schema');
 *
 * @module schema
 */

/* global __*/

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

/**
 * Creates dynamic schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key schema resource descriptor key.
 * @param {string} params.type schema type.
 * @param {string} [params.resource] Schema resource value.
 *
 * @returns {string} created resource.
 */
exports.create = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.CreateDynamicSchemaHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Fetches dynamic schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key schema resource descriptor key.
 * @param {string} params.type schema type.
 *
 * @returns {string} fetched resource.
 */
exports.get = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.GetDynamicSchemaHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    return bean.execute();
};

/**
 * Removes dynamic schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key schema resource descriptor key.
 * @param {string} params.type schema type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
exports.delete = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.DeleteDynamicSchemaHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    return __.toNativeObject(bean.execute());
};



