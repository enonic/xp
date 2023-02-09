/* global __*/

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.AuditLogCleanupTaskHandler');
    bean.setAgeThreshold(nullOrValue(params.ageThreshold));
    bean.setTaskId(nullOrValue(taskId));
    bean.execute();
};
