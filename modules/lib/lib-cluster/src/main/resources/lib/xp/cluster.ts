declare global {
    interface XpLibraries {
        '/lib/xp/cluster': typeof import('./cluster');
    }
}

interface ClusterIsMasterHandler {
    isMaster(): boolean;
}

interface ClusterIsLeaderHandler {
    setApplicationKey(applicationKey: string | null): void;
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
 * @param {string} [applicationKey] - Optional application key to scope leadership to members running the application.
 * @returns {boolean} true if the current node is leader; false otherwise.
 */
export function isLeader(applicationKey?: string): boolean {
    const bean: ClusterIsLeaderHandler = __.newBean<ClusterIsLeaderHandler>('com.enonic.xp.lib.cluster.ClusterIsLeaderHandler');
    bean.setApplicationKey(applicationKey ?? null);
    return __.toNativeObject(bean.isLeader());
}
