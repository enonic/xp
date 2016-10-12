/**
 * Functions to find and manipulate nodes.
 *
 * @example
 * var nodeLib = require('/lib/xp/node');
 *
 * @module lib/xp/node
 */

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

/**
 * This function creates a node.
 *


 * @example-ref examples/node/create.js
 *
 *
 * @returns {object} Node created as JSON.
 */
exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.node.CreateNodeHandler');
    params = params || {};
    bean.params = __.toScriptValue(params);

    return __.toNativeObject(bean.execute());
};

/**
 * This function fetches a node.
 *
 * @example-ref examples/node/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the node.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {object} The content (as JSON) fetched from the repository.
 */
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.node.GetNodeHandler');
    bean.key = required(params, 'key');
    bean.branch = nullOrValue(params.branch);
    return __.toNativeObject(bean.execute());
};


/**
 * This command queries nodes.
 *
 * @example-ref examples/node/query.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} params.query Query expression.
 * @param {string} [params.sort] Sorting expression.
 * @param {string} [params.aggregations] Aggregations expression.
 * @returns {boolean} Result of query.
 */
exports.query = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.node.QueryNodeHandler');
    bean.start = params.start;
    bean.count = params.count;
    bean.query = nullOrValue(params.query);
    bean.sort = nullOrValue(params.sort);
    bean.aggregations = __.toScriptValue(params.aggregations);
    return __.toNativeObject(bean.execute());
};