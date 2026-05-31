var portalLib = require('/lib/xp/portal');

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

// Boolean directive
csp.upgradeInsecureRequests();

// Sandbox flags
csp.sandbox(portalLib.SandboxFlag.ALLOW_SCRIPTS, portalLib.SandboxFlag.ALLOW_SAME_ORIGIN);

// Hash sources for script-src / style-src -- digest inline content, or a precomputed digest
csp.scriptSrcSha({content: 'window.foo = 42;'});                  // sha256 of the content
csp.styleSrcSha({content: 'body { color: red; }', algo: 'sha384'}); // choose the algorithm
csp.scriptSrcSha({hash: 'AbCdEf0123...', algo: 'sha384'});         // precomputed base64 digest

// Request-scoped nonce (lazy; same value on subsequent calls), valid only on script-src/style-src
var nonce = csp.scriptSrcNonce();     // -> script-src 'nonce-...'
csp.styleSrcNonce();                  // -> style-src  'nonce-...' (same value)

// Escape hatches for less-common / future directives
csp.add('require-trusted-types-for', "'script'");
csp.override('script-src', portalLib.CspSource.SELF);
// END
