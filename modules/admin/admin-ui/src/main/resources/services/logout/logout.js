var auth = require('/lib/xp/auth');

function handleGet(req) {
    auth.logout();

    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var launcherUrl = uriScriptHelper.generateAdminToolUri();
    return {
        redirect: launcherUrl + '/'
    }
}
exports.get = handleGet;
