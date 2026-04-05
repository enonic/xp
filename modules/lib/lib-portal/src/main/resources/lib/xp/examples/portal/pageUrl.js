var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
var url = portalLib.pageUrl({
    path: '/my/page',
    params: {
        a: 1,
        b: [1, 2]
    },
    project: 'myproject',
    branch: 'draft'
});
// END

t.assertEquals('PageUrlParams{type=server, params={a=[1], b=[1, 2]}, path=/my/page, project=myproject, branch=draft}', url);
