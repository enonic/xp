/* global __*/

function intOrNull(value) {
    return value === undefined || value === null ? null : parseInt(value, 10);
}

exports.run = function (params) {
    var bean = __.newBean('com.enonic.xp.app.system.ExportTaskHandler');
    bean.setRepository(__.nullOrValue(params.repository));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setNodePath(__.nullOrValue(params.nodePath));
    bean.setExportName(__.nullOrValue(params.exportName));
    bean.setBatchSize(intOrNull(params.batchSize));
    bean.execute();
};
