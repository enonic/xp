/**
 * Functions to find and manipulate content.
 *
 * @example
 * var contentLib = require('/lib/xp/node');
 *
 * @module lib/xp/node
 */

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.node.CreateNodeHandler');

    params = params || {};
    bean.name = nullOrValue(params._name);
    bean.parentPath = nullOrValue(params._parentPath);
    bean.data = __.toScriptValue(params.data);
    bean.params = __.toScriptValue(params);

    return __.toNativeObject(bean.execute());
};
