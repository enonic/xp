var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// The site URLs belong to, in place of a site request: resolved once for every URL of that site
var scope = portalLib.portalScope({
    key: '/my-site',
    project: 'myproject',
    branch: 'master'
});

// The site's configured Base URL, or the origin the frontend serves the site from
var origin = scope.baseUrl || 'https://www.example.com';

var post = portalLib.pageUrlParts({path: '/my-site/posts/first-post', scope: scope});
var url = origin + post.path + post.queryString;
// END

t.assertEquals('https://www.example.com', origin);
t.assertEquals('https://www.example.com/posts/first-post', url);
