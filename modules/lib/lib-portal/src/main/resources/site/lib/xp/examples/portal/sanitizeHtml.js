var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var unsafeHtml = '<p><a href="http://example.com/" onclick="stealCookies()">Link</a></p>' +
                 '<iframe src="javascript:alert(\'XSS\');"></iframe>';
var sanitizedHtml = portalLib.sanitizeHtml(unsafeHtml);
// END

// BEGIN
// Sanitized HTML returned.
var expectedHtml = '<p><a href="http://example.com/">Link</a></p>';
// END

assert.assertEquals('HTML result not equals', expectedHtml, sanitizedHtml);
