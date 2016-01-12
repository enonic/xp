var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function getAdminTools() {
    var bean = __.newBean('com.enonic.xp.admin.ui.tool.GetAdminToolsScriptBean');
    return __.toNativeObject(bean.execute());
}

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var assetsUri = uriScriptHelper.ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('launcher.html');

    //Retrieves the Admin tools
    var adminTools = getAdminTools();
    for (var i = 0; i < adminTools.length; i++) {
        if (adminTools[i].icon.indexOf("//") < 0) {
            adminTools[i].icon = portal.assetUrl({
                path: adminTools[i].icon
            });
        }
        adminTools[i].uri =
            uriScriptHelper.generateAdminToolUri(adminTools[i].key.application, adminTools[i].key.name);
    }

    //Retrieves the profile informations
    var userIconUrl = portal.assetUrl({path: "icons/user.svg"});
    var logoutServiceUrl = portal.serviceUrl({service: 'logout'});
    var user = auth.getUser();

    var params = {
        assetsUri: assetsUri,
        baseUri: '',
        xpVersion: app.version,
        app: 'applications',
        adminTools: adminTools,
        userIconUrl: userIconUrl,
        user: user,
        logoutServiceUrl: logoutServiceUrl,
        homeUrl: uriScriptHelper.generateHomeUri(adminTools[0].key.application)
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;