var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function getAdminApplications() {
    var bean = __.newBean('com.enonic.xp.admin.ui.adminapp.GetAdminApplicationsScriptBean');
    return __.toNativeObject(bean.execute());
}

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.adminapp.UriScriptHelper");
    var assetsUri = uriScriptHelper.ADMIN_ASSETS_URI_PREFIX;
    var view = resolve('app-launcher.html');

    var adminApplications = getAdminApplications();
    for (var i = 0; i < adminApplications.length; i++) {
        if (adminApplications[i].icon.indexOf("//") < 0) {
            adminApplications[i].icon = portal.assetUrl({
                path: adminApplications[i].icon
            });
        }
        adminApplications[i].uri =
            uriScriptHelper.generateAdminApplicationUri(adminApplications[i].key.application, adminApplications[i].key.name);
    }

    var params = {
        assetsUri: assetsUri,
        baseUri: '',
        xpVersion: app.version,
        app: 'applications',
        adminApplications: adminApplications
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;