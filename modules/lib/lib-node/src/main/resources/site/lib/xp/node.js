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
 * This function fetches nodes. If key is defined, the fetched node will be returned as JSON (null if not found).
 * If keys is defined, the fetched nodes will be return as a JSON array.
 *
 * @example-ref examples/node/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.key] Path or id to the node.
 * @param {string} [params.keys] Path or id array to the nodes.
 * @param {string} [params.branch] Set by portal, depending on context, to either draft or master. May be overridden, but this is not recommended. Default is the current branch set in portal.
 *
 * @returns {object} The node or node array (as JSON) fetched from the repository.
 */
exports.get = function (params) {
    if (params.key === undefined && params.keys === undefined) {
        throw "Parameter 'key' or 'keys' is required";
    }

    var bean = __.newBean('com.enonic.xp.lib.node.GetNodeHandler');
    bean.key = params.key ? params.key : null;
    bean.keys = params.keys ? params.keys : [];
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