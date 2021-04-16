/* global __*/

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
}

exports.run = function (params) {
    var bean = __.newBean('com.enonic.xp.app.system.VacuumTaskHandler');
    bean.ageThreshold = nullOrValue(params.ageThreshold);
    bean.tasks = nullOrValue(params.tasks);
    bean.execute();
};