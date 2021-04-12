/* global __*/

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.run = function (params) {
    var bean = __.newBean('com.enonic.xp.app.system.AuditLogCleanupTaskHandler');
    bean.ageThreshold = nullOrValue(params.ageThreshold);
    bean.execute();
};