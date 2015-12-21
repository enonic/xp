var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');

function getAdminApplications() {
    var bean = __.newBean('com.enonic.xp.admin.ui.adminapp.GetAdminApplicationsHandler');
    return __.toNativeObject(bean.execute());
}

function handleGet(req) {
    var view = resolve('app-launcher.html');
    var imgIconView = resolve('img-icon.html');
    var adminApplications = getAdminApplications();

    for (var i = 0; i < adminApplications.length; i++) {
        if (adminApplications[i].icon.indexOf("//") < 0) {
            adminApplications[i].icon = portal.assetUrl({
                path: adminApplications[i].icon
            });
        }
    }

    var params = {
        assetsUri: '/admin/assets/0',
        baseUri: '',
        xpVersion: '6.4.0-SNAPSHOT',
        app: 'applications',
        adminApplications: adminApplications
    };
    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;