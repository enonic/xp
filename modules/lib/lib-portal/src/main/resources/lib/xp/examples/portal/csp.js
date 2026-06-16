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

// Request-scoped nonce (lazy; same value on subsequent calls), valid only on the
// script-src/style-src directives and their -elem variants.
// Stamp the returned value on the matching inline tag: <script nonce="...">
var nonce = csp.nonceScriptSrc();     // -> script-src      'nonce-...'
csp.nonceStyleSrc();                  // -> style-src       'nonce-...' (same value)
csp.nonceScriptSrcElem();            // -> script-src-elem 'nonce-...' (same value)
csp.nonceStyleSrcElem();             // -> style-src-elem  'nonce-...' (same value)

// Union extra directives from a raw header value (e.g. operator config) on top of the policy,
// without restating it -- existing directives are extended, the nonce above is kept
csp.merge("connect-src https://api.example.com; img-src https://cdn.example.com");

// Escape hatches for less-common / future directives
csp.add('require-trusted-types-for', "'script'");
csp.override('script-src', portalLib.CspSource.SELF);

// Inspect what is currently declared -- null if no contributor set it yet
csp.directive('script-src');  // -> ["'self'"]

// Report-only companion -- a separate handle via portal.cspReportOnly(), same fluent API, emitted
// as Content-Security-Policy-Report-Only, shares the request nonce
portalLib.cspReportOnly().scriptSrc(portalLib.CspSource.SELF, portalLib.CspSource.REPORT_SAMPLE);

// The Content-Security-Policy header is composed and emitted automatically
// at response-flush time
// END

assert.assertTrue('nonce is a non-empty string', typeof nonce === 'string' && nonce.length > 0);
