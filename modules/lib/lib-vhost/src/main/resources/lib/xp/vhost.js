/**
 * Functions to find virtual host.
 *
 * @example
 * var vhostLib = require('/lib/xp/vhost');
 *
 * @module vhost
 */

/* global __ */

exports.isEnabled = function () {
    const bean = __.newBean('com.enonic.xp.lib.vhost.VirtualHostHandler');
    return __.toNativeObject(bean.isEnabled());
};

/**
 * This function returns virtual hosts.
 *
 * @returns {object} An object with all the virtual hosts.
 */
exports.list = function () {
    const bean = __.newBean('com.enonic.xp.lib.vhost.VirtualHostHandler');
    return __.toNativeObject(bean.getVirtualHosts());
};
