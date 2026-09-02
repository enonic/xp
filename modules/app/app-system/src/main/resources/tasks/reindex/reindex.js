/* global __*/

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.ReindexTaskHandler');
    bean.setRepository(__.nullOrValue(params.repository));
    bean.setBranches(__.nullOrValue(params.branches));
    bean.setInitialize(params.initialize === true);
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
