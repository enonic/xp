declare global {
    interface XpLibraries {
        '/lib/xp/cluster': typeof import('./cluster');
    }
}

interface ClusterIsMasterHandler {
    isMaster(): boolean;
}

/**
 * Cluster related functions.
 *
 * @example
 * var clusterLib = require('/lib/xp/cluster');
 *
 * @module cluster
 */

/**
 * Tests whether the current node is the master node in the cluster.
 *
 * @example-ref examples/cluster/isMaster.js
 *
 * @returns {boolean} true if the current node is master; false otherwise.
 */
export function isMaster(): boolean {
    const bean = __.newBean<ClusterIsMasterHandler>('com.enonic.xp.lib.cluster.ClusterIsMasterHandler');
    return __.toNativeObject(bean.isMaster());
}
