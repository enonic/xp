var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// The site URLs belong to, resolved once for every URL of the same request
var base = portalLib.urlBase({
    key: '/my-site',
    project: 'myproject',
    branch: 'master'
});

// The site's configured Base URL, or the origin the frontend serves the site from
var origin = base.baseUrl || 'https://www.example.com';

var post = portalLib.pageUrlParts({path: '/my-site/posts/first-post', base: base});
var url = origin + post.path + post.queryString;
// END

t.assertEquals('https://www.example.com', origin);
t.assertEquals('https://www.example.com/posts/first-post', url);
