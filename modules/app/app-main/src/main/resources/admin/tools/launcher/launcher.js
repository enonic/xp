var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');
var i18n = require('/lib/xp/i18n');
var admin = require('/lib/xp/admin');

var uriHelperBean = __.newBean('com.enonic.xp.app.main.UriScriptHelper');
var adminToolsBean = __.newBean('com.enonic.xp.app.main.GetAdminToolsScriptBean');

function getAdminTools() {
    var result = __.toNativeObject(adminToolsBean.execute());
    return result.sort(function (tool1, tool2) {
        return (tool1.displayName > tool2.displayName) ? 1 : -1;
    });
}

function generateAdminToolUri(app, name) {
    return uriHelperBean.generateAdminToolUri(app, name);
}

exports.get = function () {

    var adminTools = getAdminTools();
    for (var i = 0; i < adminTools.length; i++) {
        adminTools[i].appId = adminTools[i].key.application;
        adminTools[i].uri = generateAdminToolUri(adminTools[i].key.application, adminTools[i].key.name);
    }

    var userIconUrl = portal.assetUrl({path: "icons/user.svg"});
    // var launcherCss = portal.assetUrl({path: "styles/_launcher.css"});
    var logoutUrl = portal.logoutUrl({
        redirect: portal.url({path: "/admin/tool", type: "absolute"})
    });

    var user = auth.getUser();
    var locale = admin.getLocale();

    var view = resolve('./launcher.html');
    var params = {
        xpVersion: app.version,
        appId: 'launcher',
        adminTools: adminTools,
        userIconUrl: userIconUrl,
        user: user,
        logoutUrl: logoutUrl,
        homeUrl: uriHelperBean.generateAdminToolUri(),
        installation: uriHelperBean.getInstallation() || "Tools",
        // launcherCss: launcherCss,
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
        }),
        adminAssetsUri: admin.getAssetsUri(),
        assetsUri: portal.assetUrl({
            path: ''
        })
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
};
