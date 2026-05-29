var portalLib = require('/lib/xp/portal');

// BEGIN
var csp = portalLib.getCsp();

// Typed source-list directives -- variadic typed sources
csp.defaultSrc(portalLib.CspSource.NONE);
csp.scriptSrc(portalLib.CspSource.SELF, 'https://cdn.example.com');
csp.styleSrc(portalLib.CspSource.SELF);
csp.imgSrc(portalLib.CspSource.SELF, 'data:');

// Restrictive / single-list directives
csp.frameAncestors(portalLib.CspSource.NONE);
csp.baseUri(portalLib.CspSource.SELF);
csp.formAction(portalLib.CspSource.SELF);

// Boolean directive
csp.upgradeInsecureRequests();

// Sandbox flags
csp.sandbox(portalLib.SandboxFlag.ALLOW_SCRIPTS, portalLib.SandboxFlag.ALLOW_SAME_ORIGIN);

// Hash convenience -- SHA-256 of UTF-8 bytes, or precomputed digest
csp.addScriptSrcSha('window.foo = 42;');
csp.addStyleSrcSha('body { color: red; }');

// Request-scoped nonce (lazy; same value on subsequent calls)
var nonce = csp.getNonce();

// Allow inline style nonces
csp.add('style-src', ["'nonce-" + nonce + "'"]);

// Escape hatches for less-common / future directives
csp.add('require-trusted-types-for', ["'script'"]);
csp.set('script-src', [portalLib.CspSource.SELF]);
// END
