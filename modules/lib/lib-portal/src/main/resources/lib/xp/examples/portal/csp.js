var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var csp = portalLib.csp();

// Deny-all baseline -- a starting point you then open up directive by directive:
//   csp.strict();  // default-src 'none', base-uri 'none', frame-ancestors 'none'

// Typed source-list directives -- variadic typed sources
csp.defaultSrc(portalLib.CspSource.NONE);
csp.scriptSrc(portalLib.CspSource.SELF, 'https://cdn.example.com');
csp.styleSrc(portalLib.CspSource.SELF);
csp.imgSrc(portalLib.CspSource.SELF, portalLib.CspSource.DATA);

// Restrictive / single-list directives
csp.frameAncestors(portalLib.CspSource.NONE);
csp.baseUri(portalLib.CspSource.SELF);
csp.formAction(portalLib.CspSource.SELF);

// Boolean directive -- add() with no sources registers a bare directive
csp.add('upgrade-insecure-requests');

// Sandbox flags
csp.sandbox(portalLib.SandboxFlag.ALLOW_SCRIPTS, portalLib.SandboxFlag.ALLOW_SAME_ORIGIN);

// Hash sources for script-src / style-src -- digest inline content, or a precomputed digest
csp.shaScriptSrc({content: 'window.foo = 42;'});                  // sha256 of the content
csp.shaStyleSrc({content: 'body { color: red; }', algo: 'sha384'}); // choose the algorithm
csp.shaScriptSrc({hash: 'AbCdEf0123...', algo: 'sha384'});         // precomputed base64 digest

// Request-scoped nonce (lazy; same value on subsequent calls), valid only on script-src/style-src.
// Stamp the returned value on the matching inline tag: <script nonce="...">
var nonce = csp.nonceScriptSrc();     // -> script-src 'nonce-...'
csp.nonceStyleSrc();                  // -> style-src  'nonce-...' (same value)

// Escape hatches for less-common / future directives
csp.add('require-trusted-types-for', "'script'");
csp.override('script-src', portalLib.CspSource.SELF);

// The header is emitted automatically at response-flush time; build() renders the
// current header value for pipelines that compose the response themselves
var headerValue = csp.build();
// END

assert.assertTrue('nonce is a non-empty string', typeof nonce === 'string' && nonce.length > 0);
assert.assertTrue('policy contains the nonce', headerValue.indexOf("'nonce-" + nonce + "'") >= 0);
assert.assertTrue('override replaced script-src', headerValue.indexOf("script-src 'self';") >= 0);
