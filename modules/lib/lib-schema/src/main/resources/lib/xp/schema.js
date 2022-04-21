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
 * Creates dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Schema resource name.
 * @param {string} params.type Schema type.
 * @param {string} [params.resource] Schema resource value.
 *
 * @returns {string} created resource.
 */
exports.createSchema = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.CreateDynamicContentSchemaHandler');
    bean.setName(required(params, 'name'));
    bean.setType(required(params, 'type'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Creates dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} [params.resource] Component resource value.
 *
 * @returns {string} created resource.
 */
exports.createComponent = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.CreateDynamicComponentHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Creates dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 * @param {string} [params.resource] Styles resource value.
 *
 * @returns {string} created resource.
 */
exports.createStyles = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.CreateDynamicStylesHandler');
    bean.setKey(required(params, 'key'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Fetches dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {string} fetched resource.
 */
exports.getSchema = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.GetDynamicContentSchemaHandler');
    bean.setName(required(params, 'name'));
    bean.setType(required(params, 'type'));
    return bean.execute();
};

/**
 * Fetches dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {string} fetched resource.
 */
exports.getComponent = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.GetDynamicComponentHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    return bean.execute();
};

/**
 * Fetches dynamic site schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {string} fetched resource.
 */
exports.getSite = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.GetDynamicSiteHandler');
    bean.setKey(required(params, 'key'));
    return bean.execute();
};

/**
 * Fetches dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key application key.
 *
 * @returns {string} fetched resource.
 */
exports.getStyles = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.GetDynamicStylesHandler');
    bean.setKey(required(params, 'key'));
    return bean.execute();
};

/**
 * Removes dynamic schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
exports.deleteSchema = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.DeleteDynamicContentSchemaHandler');
    bean.setName(required(params, 'name'));
    bean.setType(required(params, 'type'));
    return __.toNativeObject(bean.execute());
};

/**
 * Removes dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
exports.deleteComponent = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.DeleteDynamicComponentHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    return __.toNativeObject(bean.execute());
};

/**
 * Removes dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 *
 * @returns {boolean} true if succeeded, false otherwise.
 */
exports.deleteStyles = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.DeleteDynamicStylesHandler');
    bean.setKey(required(params, 'key'));
    return __.toNativeObject(bean.execute());
};

/**
 * Updates dynamic content schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Content schema resource name.
 * @param {string} params.type Content schema type.
 * @param {string} [params.resource] Schema resource value.
 *
 * @returns {string} created resource.
 */
exports.updateSchema = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.UpdateDynamicContentSchemaHandler');
    bean.setName(required(params, 'name'));
    bean.setType(required(params, 'type'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Updates dynamic component resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Component resource descriptor key.
 * @param {string} params.type Component type.
 * @param {string} [params.resource] Component resource value.
 *
 * @returns {string} created resource.
 */
exports.updateComponent = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.UpdateDynamicComponentHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Updates dynamic site schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 * @param {string} [params.resource] Site schema resource value.
 *
 * @returns {string} created resource.
 */
exports.updateSite = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.UpdateDynamicSiteHandler');
    bean.setKey(required(params, 'key'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Updates dynamic styles schema resource.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 * @param {string} [params.resource] Styles schema resource value.
 *
 * @returns {string} created resource.
 */
exports.updateStyles = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.UpdateDynamicStylesHandler');
    bean.setKey(required(params, 'key'));
    bean.setResource(__.nullOrValue(params.resource));
    return bean.execute();
};

/**
 * Fetches dynamic component resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 * @param {string} params.type Component type.
 *
 * @returns {string} fetched resources.
 */
exports.listComponents = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.ListDynamicComponentsHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    return __.toNativeObject(bean.execute());
};

/**
 * Fetches dynamic content schemas resources.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Application key.
 * @param {string} params.type Content schema type.
 *
 * @returns {string} fetched resources.
 */
exports.listSchemas = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.schema.ListDynamicSchemasHandler');
    bean.setKey(required(params, 'key'));
    bean.setType(required(params, 'type'));
    return __.toNativeObject(bean.execute());
};



