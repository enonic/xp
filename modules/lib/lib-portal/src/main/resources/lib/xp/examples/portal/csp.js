var portalLib = require('/lib/xp/portal');

// BEGIN
var csp = portalLib.csp();

// One-shot strict baselines -- pick one as a starting point, then open up what you need:
//   csp.strict();         // deny-all: default-src 'none', base-uri 'none', frame-ancestors 'none'
//   csp.strictDynamic();  // nonce + 'strict-dynamic' script policy (web.dev), then csp.nonceScriptSrc()

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
csp.addScriptSrcSha({content: 'window.foo = 42;'});                  // sha256 of the content
csp.addStyleSrcSha({content: 'body { color: red; }', algo: 'sha384'}); // choose the algorithm
csp.addScriptSrcSha({hash: 'AbCdEf0123...', algo: 'sha384'});         // precomputed base64 digest

// Request-scoped nonce (lazy; same value on subsequent calls). Wire it into script-src,
// style-src, or both -- the only directives a nonce is valid for.
var nonce = csp.nonceScriptSrc();     // -> script-src 'nonce-...'
csp.nonceStyleSrc();                  // -> style-src  'nonce-...' (same value)
// var nonce = csp.nonce();          // both at once

// Escape hatches for less-common / future directives
csp.add('require-trusted-types-for', "'script'");
csp.set('script-src', portalLib.CspSource.SELF);
// END
