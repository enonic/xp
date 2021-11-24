/* global __*/

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.run = function (params) {
    var bean = __.newBean('com.enonic.xp.app.system.VacuumTaskHandler');
    bean.setAgeThreshold(nullOrValue(params.ageThreshold));
    bean.setTasks(nullOrValue(params.tasks));
    bean.execute();
};