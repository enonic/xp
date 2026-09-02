/* global __*/

function intOrNull(value) {
    return value === undefined || value === null ? null : parseInt(value, 10);
}

exports.run = function (params, taskId) {
    var bean = __.newBean('com.enonic.xp.app.system.DumpTaskHandler');
    bean.setName(__.nullOrValue(params.name));
    bean.setIncludeVersions(params.includeVersions === true);
    bean.setMaxAge(intOrNull(params.maxAge));
    bean.setMaxVersions(intOrNull(params.maxVersions));
    bean.setRepositories(__.nullOrValue(params.repositories));
    bean.setTaskId(__.nullOrValue(taskId));
    bean.execute();
};
