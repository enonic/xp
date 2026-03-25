/* global __*/

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.SnapshotTaskHandler');
    bean.setSnapshotName(__.nullOrValue(params.snapshotName));
    bean.setRepositoryId(__.nullOrValue(params.repositoryId));
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
