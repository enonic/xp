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

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.LoadTaskHandler');
    bean.setName(__.nullOrValue(params.name));
    bean.setUpgrade(params.upgrade === true);
    bean.setRepositories(toArray(params.repositories));
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
