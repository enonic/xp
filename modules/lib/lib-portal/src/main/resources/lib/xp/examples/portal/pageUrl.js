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

t.assertEquals('/site/mocksite//my/page', url);
