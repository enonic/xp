/* global __*/

exports.run = function (params) {
    var bean = __.newBean('com.enonic.xp.app.system.UpgradeTaskHandler');
    bean.setName(__.nullOrValue(params.name));
    bean.execute();
};
