var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

var expectedHtml = '<p><a href="http://example.com/">Link</a></p>';

exports.sanitizeHtml = function () {
    var html = '<p><a href="http://example.com/" onclick="stealCookies()">Link</a></p>';
    var result = portal.sanitizeHtml(html);
    assert.assertEquals(expectedHtml, result);
};
