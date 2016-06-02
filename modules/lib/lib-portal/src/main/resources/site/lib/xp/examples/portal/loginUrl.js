var portalLib = require('/lib/xp/portal');

// BEGIN
var redirectUrl = portalLib.pageUrl({
    path: '/my/page'
});
var url = portalLib.loginUrl({
    redirect: redirectUrl
});
// END
