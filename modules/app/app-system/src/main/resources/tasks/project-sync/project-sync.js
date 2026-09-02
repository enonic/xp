/* global __*/

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.ProjectSyncTaskHandler');
    bean.setProjects(__.nullOrValue(params.projects));
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
