var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// The site the URLs belong to, in place of a site request: resolve it once, and pass it to every call for that site
var base = portalLib.urlBase({
    key: '/my-site',
    project: 'myproject',
    branch: 'master'
});

// Process rich text of a site: each link and macro in the HTML is a placeholder, rendered from its entry
var result = portalLib.processHtmlParts({
    value: '<a href="content://123456">Post</a>[youtube videoid="abc"/]',
    base: base
});

// The site's configured Base URL, or the origin the frontend serves the site from
var origin = result.baseUrl || 'https://www.example.com';
var link = result.links[0];
// a link that does not resolve has no parts
var href = link.type === 'content' && link.page ? origin + link.page.path + link.page.queryString : null;

// a macro of an application of the site, rendered by the frontend in place of its editor-macro element
var macro = result.macros[0];
var embedUrl = macro.descriptor === 'com.example.myapp:youtube' ? 'https://www.youtube.com/embed/' + macro.params.videoId[0] : null;
// END

t.assertEquals('<a href="/posts/first-post" data-link-ref="ref">Post</a>' +
               '<editor-macro data-macro-name="youtube" data-macro-ref="macroref"></editor-macro>',
    result.html);
t.assertEquals(null, result.baseUrl);
t.assertEquals('123456', result.links[0].contentId);
t.assertEquals('content', result.links[0].type);
t.assertEquals('https://www.example.com/posts/first-post', href);
t.assertEquals('macroref', macro.ref);
t.assertJsonEquals({videoId: ['abc']}, macro.params);
t.assertEquals('', macro.body);
t.assertEquals('https://www.youtube.com/embed/abc', embedUrl);
