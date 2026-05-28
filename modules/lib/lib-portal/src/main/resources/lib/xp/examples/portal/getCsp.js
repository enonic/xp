var portalLib = require('/lib/xp/portal');

// BEGIN
var csp = portalLib.getCsp();

// Add sources to a directive (unioned, deduped)
csp.add('script-src', ["'self'", 'https://cdn.example.com']);

// Reset the directive's source list
csp.set('default-src', ["'none'"]);

// Add a SHA-256 of an inline script's UTF-8 content
csp.addSha('script-src', 'window.foo = 42;');

// Opt extra directives into receiving the request nonce
csp.applyNonceTo(['style-src']);

// Read the request-scoped nonce (lazy; same value on subsequent calls)
var nonce = csp.getNonce();
// END
