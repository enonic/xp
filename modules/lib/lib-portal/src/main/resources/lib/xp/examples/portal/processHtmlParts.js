var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// Process rich text of a site: each link in the HTML is a placeholder, rendered from the parts of its entry
var result = portalLib.processHtmlParts({
    value: '<a href="content://123456">Post</a>',
    base: {
        path: '/my-site',
        project: 'myproject',
        branch: 'master'
    }
});

// The site's configured Base URL, or the origin the frontend serves the site from
var origin = result.baseUrl || 'https://www.example.com';
var link = result.links[0];
var href = link.type === 'content' ? origin + link.page.path + link.page.queryString : null;
// END

t.assertEquals('<a href="/posts/first-post" data-link-ref="ref">Post</a>', result.html);
t.assertEquals(null, result.baseUrl);
t.assertEquals('123456', result.links[0].contentId);
t.assertEquals('content', result.links[0].type);
t.assertEquals('https://www.example.com/posts/first-post', href);
