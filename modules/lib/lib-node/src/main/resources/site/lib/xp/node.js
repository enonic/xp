/**
 * Functions to find and manipulate nodes.
 *
 * @example
 * var nodeLib = require('/lib/xp/node');
 *
 * @module lib/xp/node
 */

var GeoPointType = Java.type("com.enonic.xp.util.GeoPoint");
var InstantType = Java.type("java.time.Instant");
var LocalDateType = Java.type("java.time.LocalDate");
var LocalDateTimeType = Java.type("java.time.LocalDateTime");
var LocalTimeType = Java.type("java.time.LocalTime");
var ReferenceType = Java.type("com.enonic.xp.util.Reference");


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

exports.geoPoint = function (lat, lon) {
    return new GeoPointType(lat, lon);
};

exports.instant = function (value) {
    return InstantType.parse(value);
};

exports.reference = function (value) {
    return ReferenceType.from(value);
};

exports.localDateTime = function (value) {
    return LocalDateTimeType.parse(value);
};

exports.localDate = function (value) {
    return LocalDateType.parse(value);
};

exports.localTime = function (value) {
    return LocalTimeType.parse(value);
};
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
 * This function fetches nodes. If key is defined, the fetched node will be returned as a JSON object or null if not found.
 * If keys is defined, the fetched nodes will be return as a JSON array.
 *
 * @example-ref examples/node/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.key] Path or id to the node.
 * @param {string[]} [params.keys] Path or id array to the nodes.
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
    return __.toNativeObject(bean.execute());
};


/**
 * This function deletes nodes.
 *
 * @example-ref examples/node/delete.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.key] Path or id to the node.
 * @param {string[]} [params.keys] Path or id array to the nodes.
 *
 * @returns {string[]} Id array of deleted nodes.
 */
exports.delete = function (params) {
    if (params.key === undefined && params.keys === undefined) {
        throw "Parameter 'key' or 'keys' is required";
    }
    var bean = __.newBean('com.enonic.xp.lib.node.DeleteNodeHandler');
    bean.key = params.key ? params.key : null;
    bean.keys = params.keys ? params.keys : [];
    return __.toNativeObject(bean.execute());
};


/**
 * Rename a node or move it to a new path.
 *
 * @example-ref examples/node/move-1.js
 * @example-ref examples/node/move-2.js
 * @example-ref examples/node/move-3.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.source Path or id of the node to be moved or renamed.
 * @param {string} params.target New path or name for the node. If the target ends in slash '/', it specifies the parent path where to be moved. Otherwise it means the new desired path or name for the node.
 *
 * @returns {boolean} True if the node was successfully moved or renamed, false otherwise.
 */
exports.move = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.node.MoveNodeHandler');
    bean.source = required(params, 'source');
    bean.target = required(params, 'target');
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