/* global __*/

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.RestoreTaskHandler');
    bean.setSnapshotName(__.nullOrValue(params.snapshotName));
    bean.setRepositoryId(__.nullOrValue(params.repositoryId));
    bean.setLatest(params.latest === 'true');
    bean.setForce(params.force === 'true');
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
