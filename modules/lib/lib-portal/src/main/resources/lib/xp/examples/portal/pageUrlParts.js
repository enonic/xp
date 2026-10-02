var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// Parts of the URL of a page, relative to the site it belongs to
var parts = portalLib.pageUrlParts({
    path: '/my-site/posts/first-post',
    base: {
        path: '/my-site',
        project: 'myproject',
        branch: 'master'
    },
    params: {
        a: 1
    }
});

// The site's configured Base URL, or the origin the frontend serves the site from
var url = (parts.baseUrl || 'https://www.example.com') + parts.path + parts.queryString;
// END

t.assertEquals(null, parts.baseUrl);
t.assertEquals('/posts/first-post', parts.path);
t.assertEquals('?a=1', parts.queryString);
t.assertEquals('https://www.example.com/posts/first-post?a=1', url);
