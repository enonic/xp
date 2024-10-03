declare global {
    interface XpLibraries {
        '/lib/xp/vhost': typeof import('./vhost');
    }
}

interface VirtualHostHandler {
    isEnabled(): boolean;

    getVirtualHosts(): VirtualHosts;
}

export interface VirtualHosts {
    vhosts: VirtualHost[];
}

export interface IdProviderKey {
    idProviderKey: string;
}

export interface VirtualHost {
    name: string;

    source: string;

    target: string;

    host: string;

    defaultIdProviderKey?: string;

    idProviderKeys?: IdProviderKey[];
}

/**
 * Functions to find virtual host.
 *
 * @example
 * var vhostLib = require('/lib/xp/vhost');
 *
 * @module vhost
 */


/**
 * Returns value which is set for the `enabled` property in the `com.enonic.xp.web.vhost.cfg` file.
 *
 * @returns {boolean} `true` if vhost mapping is enabled, otherwise `false`.
 */
export function isEnabled(): boolean {
    const bean: VirtualHostHandler = __.newBean<VirtualHostHandler>('com.enonic.xp.lib.vhost.VirtualHostHandler');
    return __.toNativeObject(bean.isEnabled());
}

/**
 * This function returns virtual hosts.
 *
 * @returns {VirtualHosts} An object with all the virtual hosts.
 */
export function list(): VirtualHosts {
    const bean: VirtualHostHandler = __.newBean<VirtualHostHandler>('com.enonic.xp.lib.vhost.VirtualHostHandler');
    return __.toNativeObject(bean.getVirtualHosts());
}
