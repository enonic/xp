/**
 * Export related functions.
 *
 * @example
 * var exportLib = require('/lib/xp/export');
 *
 * @module export
 */

declare global {
    interface XpLibraries {
        '/lib/xp/export': typeof import('./export');
    }
}

import type {ResourceKey} from '@enonic-types/core';

export type {ResourceKey} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw Error(`Parameter '${String(name)}' is required`);
    }
}

export interface ImportNodesParams {
    source: string | object;
    targetNodePath: string;
    xslt?: string | ResourceKey;
    xsltParams?: unknown;
    includeNodeIds?: boolean;
    includePermissions?: boolean;
    nodeResolved?: (numberOfNodes: number) => void;
    nodeImported?: (numberOfImportedNodes: number) => void;
}

export interface ImportNodesError {
    exception: string;
    message: string;
    stacktrace: string[];
}

export interface ImportNodesResult {
    addedNodes: string[];
    updatedNodes: string[];
    importedBinaries: string[];
    importErrors: ImportNodesError[];
}

interface ImportHandler {
    setSource(value: string | object): void;

    setTargetNodePath(value: string): void;

    setXslt(value?: string | ResourceKey | null): void;

    setXsltParams(value?: unknown | null): void;

    setIncludeNodeIds(value: boolean): void;

    setIncludePermissions(value: boolean): void;

    setNodeResolved(fn?: ((i: number) => void) | null): void;

    setNodeImported(fn?: ((i: number) => void) | null): void;

    execute(): ImportNodesResult;
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
 * @returns {ImportNodesResult} Node import results.
 */
export function importNodes(params: ImportNodesParams): ImportNodesResult {
    checkRequired(params, 'source');
    checkRequired(params, 'targetNodePath');

    const {
        source,
        targetNodePath,
        xslt,
        xsltParams,
        includeNodeIds = false,
        includePermissions = false,
        nodeResolved,
        nodeImported,
    } = params ?? {};

    const bean: ImportHandler = __.newBean<ImportHandler>('com.enonic.xp.lib.export.ImportHandler');

    bean.setSource(source);
    bean.setTargetNodePath(targetNodePath);
    bean.setXslt(__.nullOrValue(xslt));
    bean.setXsltParams(__.toScriptValue(xsltParams));
    bean.setIncludeNodeIds(includeNodeIds);
    bean.setIncludePermissions(includePermissions);
    bean.setNodeImported(__.nullOrValue(nodeImported));
    bean.setNodeResolved(__.nullOrValue(nodeResolved));

    return __.toNativeObject(bean.execute());
}

export interface ExportNodesParams {
    sourceNodePath: string;
    exportName: string;
    batchSize?: number;
    nodeResolved?: (numberOfNodes: number) => void;
    nodeExported?: (numberOfExportedNodes: number) => void;
}

export interface ExportNodesError {
    message: string;
}

export interface ExportNodesResult {
    exportedNodes: string[];
    exportedBinaries: string[];
    exportErrors: ExportNodesError[];
}

interface ExportHandler {
    setSourceNodePath(value: string): void;

    setExportName(value: string): void;

    setBatchSize(value?: number | null): void;

    setNodeExported(fn?: ((i: number) => void) | null): void;

    setNodeResolved(fn?: ((i: number) => void) | null): void;

    execute(): ExportNodesResult;
}

/**
 * Export nodes to a nodes-export.
 * Export is created in exports directory.
 *
 * @example-ref examples/export/exportNodes.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.sourceNodePath Source nodes path.
 * @param {string} params.exportName Export name.
 * @param {number} [params.batchSize=1000] Number of nodes to export in each batch.
 * @param {function} [params.nodeResolved] A function to be called before export starts with number of nodes to export.
 * @param {function} [params.nodeExported] A function to be called during export with number of nodes exported since last call.
 *
 * @returns {ExportNodesResult} Node export results.
 */
export function exportNodes(params: ExportNodesParams): ExportNodesResult {
    checkRequired(params, 'sourceNodePath');
    checkRequired(params, 'exportName');

    const {
        sourceNodePath,
        exportName,
        nodeResolved,
        batchSize,
        nodeExported,
    } = params ?? {};

    const bean: ExportHandler = __.newBean<ExportHandler>('com.enonic.xp.lib.export.ExportHandler');

    bean.setSourceNodePath(sourceNodePath);
    bean.setExportName(exportName);
    bean.setBatchSize(__.nullOrValue(batchSize));
    bean.setNodeExported(__.nullOrValue(nodeExported));
    bean.setNodeResolved(__.nullOrValue(nodeResolved));

    return __.toNativeObject(bean.execute());
}
