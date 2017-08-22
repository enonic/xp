var auth = require('/lib/xp/auth');
var mustache = require('/lib/xp/mustache');
var portal = require('/lib/xp/portal');
var admin = require('/lib/xp/admin');
var i18n = require('/lib/xp/i18n');

function handleGet(req) {
    var uriScriptHelper = Java.type("com.enonic.xp.admin.ui.tool.UriScriptHelper");
    var adminUrl = uriScriptHelper.generateAdminUri();
    var assetsUri = uriScriptHelper.generateAdminAssetsUri();
    var backgroundUri = uriScriptHelper.generateBackgroundUri();
    var view = resolve('home.html');

    var busIconUrl = portal.assetUrl({path: "icons/bus.svg"});
    var infoIconUrl = portal.assetUrl({path: "icons/info-with-circle.svg"});
    var docsIconUrl = portal.assetUrl({path: "icons/docs.svg"});
    var forumIconUrl = portal.assetUrl({path: "icons/discuss.svg"});
    var marketIconUrl = portal.assetUrl({path: "icons/market.svg"});
    
    var isLatestSnapshot = app.version.endsWith('.0.SNAPSHOT');
    var docLinkPrefix = 'http://docs.enonic.com/en/';

    if (isLatestSnapshot) {
        docLinkPrefix += 'latest';
    } else {
        var versionParts = app.version.split('.');
        docLinkPrefix += versionParts[0] + '.' + versionParts[1];
    }

    var locale = admin.getLocale();

    var dashboardIcons = [{
        src: infoIconUrl,
        cls: 'xp-about',
        caption: i18n.localize({
            key: 'home.dashboard.about',
            bundles: ['admin/i18n/common'],
            locale: locale
        })
    }, {
        src: docsIconUrl,
        cls: '',
        caption: 'Docs',
        link: docLinkPrefix + '/'
    }, {
        src: forumIconUrl,
        cls: '',
        caption: 'Discuss',
        link: 'https://discuss.enonic.com/'
    }, {
        src: marketIconUrl,
        cls: '',
        caption: i18n.localize({
            key: 'home.dashboard.market',
            bundles: ['admin/i18n/common'],
            locale: locale
        }),
        link: 'https://market.enonic.com/'
    }];

    var tourEnabled = !(app.config.tourDisabled || false);
    if (tourEnabled) {
        dashboardIcons.splice(0, 0, {
            src: busIconUrl,
            cls: 'xp-tour',
            caption: i18n.localize({
                key: 'home.dashboard.tour',
                bundles: ['admin/i18n/common'],
                locale: locale
            })
        });
    }

    var params = {
        adminUrl: adminUrl,
        assetsUri: assetsUri,
        backgroundUri: backgroundUri,
        dashboardIcons: dashboardIcons,
        baseUri: '',
        xpVersion: app.version,
        docLinkPrefix: docLinkPrefix,
        messages: admin.getPhrases(),
        tourEnabled: tourEnabled
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, params)
    };
}

exports.get = handleGet;
