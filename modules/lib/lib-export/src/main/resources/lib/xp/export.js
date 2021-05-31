/**
 * Export related functions.
 *
 * @example
 * var exportLib = require('/lib/xp/export');
 *
 * @module export
 */

/* global __*/


function required(params, name) {
    const value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }
    return value;
}

/**
 * Import nodes from a nodes-export.
 *
 * @example-ref examples/export/importNodes.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string|object} params.source Location of nodes-export directory. Can be either resource key or path relative to exports directory.
 * @param {string} params.targetNodePath Target path for imported nodes.
 * @param {string|object} [params.xslt] Location of XSLT stylesheet file used as pre-transformation of node XML files. Can be either resource key or path relative to exports directory. If specified, used as XSLT transformation for the import.
 * @param {object} [params.xsltParams] Parameters used in XSLT transformation.
 * @param {boolean} [params.includeNodeIds=false] Set to true to use node IDs from the import, false to generate new node IDs.
 * @param {boolean} [params.includePermissions=false] Set to true to use Node permissions from the import, false to use target node permissions.
 * @param {function} [params.nodeResolved] A function to be called before import starts with number of nodes to import.
 * @param {function} [params.nodeImported] A function to be called during with number of nodes imported since last call.
 *
 * @returns {object} Node import results.
 */
exports.importNodes = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.export.ImportHandler');

    bean.source = required(params, 'source');
    bean.targetNodePath = required(params, 'targetNodePath');
    bean.xslt = __.nullOrValue(params.xslt);
    bean.xsltParams = __.toScriptValue(params.xsltParams);
    bean.includeNodeIds = __.nullOrValue(params.includeNodeIds);
    bean.includePermissions = __.nullOrValue(params.includePermissions);
    bean.nodeImported = __.nullOrValue(params.nodeImported);
    bean.nodeResolved = __.nullOrValue(params.nodeResolved);

    return __.toNativeObject(bean.execute());
};
