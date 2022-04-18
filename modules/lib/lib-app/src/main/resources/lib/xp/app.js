/**
 * Application related functions.
 *
 * @example
 * var schema = require('/lib/xp/app');
 *
 * @module app
 */

/* global __*/

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

exports.list = function () {
    const bean = __.newBean('com.enonic.xp.lib.app.ListApplicationsHandler');
    return bean.execute();
};



