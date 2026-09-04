/* global __*/

/**
 * A multi-valued task property with a single value is passed to the script as a scalar.
 */
function toArray(value) {
    if (value === undefined || value === null) {
        return null;
    }
    return Array.isArray(value) ? value : [value];
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.VacuumTaskHandler');
    bean.setAgeThreshold(nullOrValue(params.ageThreshold));
    bean.setTasks(toArray(params.tasks));
    bean.setTaskId(nullOrValue(taskId));
    bean.execute();
};
