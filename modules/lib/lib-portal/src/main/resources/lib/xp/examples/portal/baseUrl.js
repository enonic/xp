const portalLib = require('/lib/xp/portal');
const assert = require('/lib/xp/testing');

// BEGIN
// The address the current site request is served from: routes of the site are relative to it
const base = portalLib.baseUrl();
const searchUrl = base + '/search';

// The same, as an absolute URL, for a canonical link
const absoluteBase = portalLib.baseUrl({
    type: 'absolute'
});
// END

assert.assertEquals('/site/mocksite', base);
assert.assertEquals('/site/mocksite/search', searchUrl);
assert.assertEquals('/site/mocksite', absoluteBase);
