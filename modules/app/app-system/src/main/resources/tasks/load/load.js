/* global __*/

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.LoadTaskHandler');
    bean.setName(__.nullOrValue(params.name));
    bean.setUpgrade(params.upgrade === true);
    bean.setRepositories(__.nullOrValue(params.repositories));
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
