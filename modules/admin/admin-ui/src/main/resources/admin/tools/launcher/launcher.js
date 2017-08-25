var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');
var i18n = require('/lib/xp/i18n');
var admin = require('/lib/xp/admin');

function getAdminTools() {
    var bean = __.newBean('com.enonic.xp.admin.ui.tool.GetAdminToolsScriptBean');
    return __.toNativeObject(bean.execute());
}

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var view = resolve('launcher.html');

    //Retrieves the Admin tools
    var adminTools = getAdminTools().sort(function (tool1, tool2) {
        return (tool1.displayName > tool2.displayName) ? 1 : -1;
    });

    for (var i = 0; i < adminTools.length; i++) {
        adminTools[i].appId = adminTools[i].key.application;
        adminTools[i].uri =
            uriScriptHelper.generateAdminToolUri(adminTools[i].key.application, adminTools[i].key.name);
    }

    //Retrieves the profile informations
    var userIconUrl = portal.assetUrl({path: "icons/user.svg"});
    var launcherCss = portal.assetUrl({path: "styles/_launcher.css"});
    var logoutUrl = portal.logoutUrl({
        redirect: portal.url({path: "/admin/tool", type: "absolute"})
    });
    var user = auth.getUser();

    var locale = admin.getLocale();

    var params = {
        xpVersion: app.version,
        appId: 'launcher',
        adminTools: adminTools,
        userIconUrl: userIconUrl,
        user: user,
        logoutUrl: logoutUrl,
        homeUrl: uriScriptHelper.generateAdminToolUri(),
        installation: uriScriptHelper.getInstallation() || "Tools",
        launcherCss: launcherCss,
        homeToolCaption: i18n.localize({
            key: 'launcher.tools.home.caption',
            bundles: ['admin/i18n/common'],
            locale: locale
        }),
        homeToolDescription: i18n.localize({
            key: 'launcher.tools.home.description',
            bundles: ['admin/i18n/common'],
            locale: locale
        }),
        logOutLink: i18n.localize({
            key: 'launcher.link.logout',
            bundles: ['admin/i18n/common'],
            locale: locale
        })
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}
exports.get = handleGet;