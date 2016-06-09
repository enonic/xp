var portalLib = require('/lib/xp/portal');

// BEGIN
var redirectionUrl = portalLib.pageUrl({
    path: '/my/page'
});
var url = portalLib.idProviderUrl({
    userStore: "system",
    redirect: redirectionUrl
});
// END
