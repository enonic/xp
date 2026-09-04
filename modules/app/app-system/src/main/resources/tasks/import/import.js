/* global __*/

exports.run = function (params) {
    var bean = __.newBean('com.enonic.xp.app.system.ImportTaskHandler');
    bean.setExportName(__.nullOrValue(params.exportName));
    bean.setRepository(__.nullOrValue(params.repository));
    bean.setBranch(__.nullOrValue(params.branch));
    bean.setNodePath(__.nullOrValue(params.nodePath));
    bean.setImportWithIds(params.importWithIds !== false);
    bean.setImportWithPermissions(params.importWithPermissions !== false);
    bean.execute();
};
