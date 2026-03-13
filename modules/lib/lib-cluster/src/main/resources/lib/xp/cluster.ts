declare global {
    interface XpLibraries {
        '/lib/xp/cluster': typeof import('./cluster');
    }
}

interface ClusterIsMasterHandler {
    isMaster(): boolean;
}

interface ClusterIsLeaderHandler {
    isLeader(): boolean;
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
    const bean: ClusterIsMasterHandler = __.newBean<ClusterIsMasterHandler>('com.enonic.xp.lib.cluster.ClusterIsMasterHandler');
    return __.toNativeObject(bean.isMaster());
}

/**
 * Tests whether the current node is the leader node in the cluster (Hazelcast-based).
 *
 * @example-ref examples/cluster/isLeader.js
 *
 * @returns {boolean} true if the current node is leader; false otherwise.
 */
export function isLeader(): boolean {
    const bean: ClusterIsLeaderHandler = __.newBean<ClusterIsLeaderHandler>('com.enonic.xp.lib.cluster.ClusterIsLeaderHandler');
    return __.toNativeObject(bean.isLeader());
}
