/**
 * Functions to find and manipulate content.
 *
 * @example
 * var contentLib = require('/lib/xp/node');
 *
 * @module lib/xp/node
 */

exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.node.CreateNodeHandler');
    params = params || {};
    bean.params = __.toScriptValue(params);

    return __.toNativeObject(bean.execute());
};
