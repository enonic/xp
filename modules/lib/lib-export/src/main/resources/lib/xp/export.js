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
 * Could be used to import node-export from exports directory or from application resource files.
 * Optionally pre-transforms node XML node files with XSLT before import.
 *
 * @example-ref examples/export/importNodes.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string|object} params.source Either name of nodes-export located in exports directory or application resource key.
 * @param {string} params.targetNodePath Target path for imported nodes.
 * @param {string|object} [params.xslt] XSLT file name in exports directory or application resource key. Used for XSLT transformation.
 * @param {object} [params.xsltParams] Parameters used in XSLT transformation.
 * @param {boolean} [params.includeNodeIds=false] Set to true to use node IDs from the import, false to generate new node IDs.
 * @param {boolean} [params.includePermissions=false] Set to true to use Node permissions from the import, false to use target node permissions.
 * @param {function} [params.nodeResolved] A function to be called before import starts with number of nodes to import.
 * @param {function} [params.nodeImported] A function to be called during import with number of nodes imported since last call.
 *
 * @returns {object} Node import results.
 */
exports.importNodes = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.export.ImportHandler');

    bean.setSource(required(params, 'source'));
    bean.setTargetNodePath(required(params, 'targetNodePath'));
    bean.setXslt(__.nullOrValue(params.xslt));
    bean.setXsltParams(__.toScriptValue(params.xsltParams));
    bean.setIncludeNodeIds(__.nullOrValue(params.includeNodeIds));
    bean.setIncludePermissions(__.nullOrValue(params.includePermissions));
    bean.setNodeImported(__.nullOrValue(params.nodeImported));
    bean.setNodeResolved(__.nullOrValue(params.nodeResolved));

    return __.toNativeObject(bean.execute());
};

/**
 * Export nodes to a nodes-export.
 * Export is created in exports directory.
 *
 * @example-ref examples/export/exportNodes.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.sourceNodePath Source nodes path.
 * @param {string} params.exportName Export name.
 * @param {boolean} [params.includeNodeIds=true] Set to true to export node IDs.
 * @param {boolean} [params.includeVersions=false] Set to true to export all node versions.
 * @param {function} [params.nodeResolved] A function to be called before export starts with number of nodes to export.
 * @param {function} [params.nodeExported] A function to be called during export with number of nodes exported since last call.
 *
 * @returns {object} Node export results.
 */
exports.exportNodes = function (params) {
    const bean = __.newBean('com.enonic.xp.lib.export.ExportHandler');
    bean.setSourceNodePath(required(params, 'sourceNodePath'));
    bean.setExportName(required(params, 'exportName'));
    bean.setIncludeNodeIds(__.nullOrValue(params.includeNodeIds));
    bean.setIncludeVersions(__.nullOrValue(params.includeVersions));
    bean.setNodeExported(__.nullOrValue(params.nodeExported));
    bean.setNodeResolved(__.nullOrValue(params.nodeResolved));

    return __.toNativeObject(bean.execute());
};
