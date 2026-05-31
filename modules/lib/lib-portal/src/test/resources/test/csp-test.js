var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.returnsObject = function () {
    var csp = portal.csp();
    assert.assertNotNull(csp);
    assert.assertEquals('function', typeof csp.add);
    assert.assertEquals('function', typeof csp.override);
    assert.assertEquals('function', typeof csp.reset);
    assert.assertEquals('function', typeof csp.strict);
    assert.assertEquals('function', typeof csp.nonceScriptSrc);
    assert.assertEquals('function', typeof csp.nonceStyleSrc);
    assert.assertEquals('function', typeof csp.defaultSrc);
    assert.assertEquals('function', typeof csp.scriptSrc);
    assert.assertEquals('function', typeof csp.styleSrc);
    assert.assertEquals('function', typeof csp.imgSrc);
    assert.assertEquals('function', typeof csp.fontSrc);
    assert.assertEquals('function', typeof csp.connectSrc);
    assert.assertEquals('function', typeof csp.mediaSrc);
    assert.assertEquals('function', typeof csp.objectSrc);
    assert.assertEquals('function', typeof csp.frameSrc);
    assert.assertEquals('function', typeof csp.workerSrc);
    assert.assertEquals('function', typeof csp.manifestSrc);
    assert.assertEquals('function', typeof csp.childSrc);
    assert.assertEquals('function', typeof csp.frameAncestors);
    assert.assertEquals('function', typeof csp.baseUri);
    assert.assertEquals('function', typeof csp.formAction);
    assert.assertEquals('function', typeof csp.upgradeInsecureRequests);
    assert.assertEquals('function', typeof csp.scriptSrcElem);
    assert.assertEquals('function', typeof csp.scriptSrcAttr);
    assert.assertEquals('function', typeof csp.styleSrcElem);
    assert.assertEquals('function', typeof csp.styleSrcAttr);
    assert.assertEquals('function', typeof csp.reportTo);
    assert.assertEquals('function', typeof csp.requireTrustedTypesFor);
    assert.assertEquals('function', typeof csp.trustedTypes);
    assert.assertEquals('function', typeof csp.reportOnly);
    assert.assertEquals('function', typeof csp.sandbox);
    assert.assertEquals('function', typeof csp.scriptSrcSha);
    assert.assertEquals('function', typeof csp.styleSrcSha);
};

exports.addSources = function () {
    var csp = portal.csp();
    csp.add('script-src', "'self'", 'https://cdn.example.com');
    csp.add('script-src', 'https://cdn.example.com', "'unsafe-inline'");
    assert.assertEquals("script-src 'self' https://cdn.example.com 'unsafe-inline'", __.toNativeObject(testInstance.policyBuild()));
};

exports.overrideReplaces = function () {
    var csp = portal.csp();
    csp.add('style-src', "'self'");
    csp.override('style-src', "'none'");
    assert.assertEquals("style-src 'none'", __.toNativeObject(testInstance.policyBuild()));
};

exports.addAfterOverride = function () {
    var csp = portal.csp();
    csp.override('img-src', "'self'");
    csp.add('img-src', 'data:');
    assert.assertEquals("img-src 'self' data:", __.toNativeObject(testInstance.policyBuild()));
};

exports.resetRemovesAll = function () {
    var csp = portal.csp();
    csp.scriptSrc(portal.CspSource.SELF);
    csp.imgSrc(portal.CspSource.SELF);
    csp.reset();
    assert.assertEquals('', __.toNativeObject(testInstance.policyBuild()));
};

exports.resetRemovesNamedDirectives = function () {
    var csp = portal.csp();
    csp.scriptSrc(portal.CspSource.SELF);
    csp.upgradeInsecureRequests();
    csp.reset('upgrade-insecure-requests');
    assert.assertEquals("script-src 'self'", __.toNativeObject(testInstance.policyBuild()));
};

exports.unsupportedAlgo = function () {
    var csp = portal.csp();
    var threw = false;
    try {
        csp.scriptSrcSha({hash: 'AbCd', algo: 'md5'});
    } catch (e) {
        threw = true;
    }
    assert.assertTrue('expected scriptSrcSha to throw on unsupported algo', threw);
};

exports.nonceScriptSrc = function () {
    var csp = portal.csp();
    var n = csp.nonceScriptSrc();
    assert.assertEquals("script-src 'nonce-" + n + "'", __.toNativeObject(testInstance.policyBuild()));
};

exports.nonceStyleSrc = function () {
    var csp = portal.csp();
    var n = csp.nonceStyleSrc();
    assert.assertEquals("style-src 'nonce-" + n + "'", __.toNativeObject(testInstance.policyBuild()));
};

exports.nonceStableAcrossMethods = function () {
    var csp = portal.csp();
    var a = csp.nonceScriptSrc();
    var b = csp.nonceStyleSrc();
    assert.assertEquals(a, b);
    assert.assertEquals(
        "script-src 'nonce-" + a + "'; style-src 'nonce-" + a + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.unsafeInlineDropsNonce = function () {
    var csp = portal.csp();
    csp.scriptSrc(portal.CspSource.UNSAFE_INLINE);
    csp.nonceScriptSrc();
    assert.assertEquals("script-src 'unsafe-inline'", __.toNativeObject(testInstance.policyBuild()));
};

exports.scriptSrcTypedAndRaw = function () {
    var csp = portal.csp();
    csp.scriptSrc(portal.CspSource.SELF, 'https://cdn.example.com');
    assert.assertEquals(
        "script-src 'self' https://cdn.example.com",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.scriptSrcAndAddUnion = function () {
    var csp = portal.csp();
    csp.scriptSrc(portal.CspSource.SELF);
    csp.add('script-src', 'https:');
    csp.scriptSrc(portal.CspSource.SELF);
    assert.assertEquals(
        "script-src 'self' https:",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.upgradeInsecureRequests = function () {
    var csp = portal.csp();
    csp.upgradeInsecureRequests();
    assert.assertEquals('upgrade-insecure-requests', __.toNativeObject(testInstance.policyBuild()));
};

exports.sandboxSingleFlag = function () {
    var csp = portal.csp();
    csp.sandbox(portal.SandboxFlag.ALLOW_SCRIPTS);
    assert.assertEquals('sandbox allow-scripts', __.toNativeObject(testInstance.policyBuild()));
};

exports.sandboxMultipleFlags = function () {
    var csp = portal.csp();
    csp.sandbox(portal.SandboxFlag.ALLOW_SCRIPTS, portal.SandboxFlag.ALLOW_SAME_ORIGIN);
    assert.assertEquals('sandbox allow-scripts allow-same-origin', __.toNativeObject(testInstance.policyBuild()));
};

exports.scriptSrcShaContent = function () {
    var csp = portal.csp();
    csp.scriptSrcSha({content: 'window.foo = 42;'});
    assert.assertEquals(
        "script-src 'sha256-" + testInstance.shaBase64('window.foo = 42;', 'SHA-256') + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.scriptSrcShaContentWithAlgo = function () {
    var csp = portal.csp();
    csp.scriptSrcSha({content: 'window.foo = 42;', algo: 'sha384'});
    assert.assertEquals(
        "script-src 'sha384-" + testInstance.shaBase64('window.foo = 42;', 'SHA-384') + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.scriptSrcShaDigest = function () {
    var csp = portal.csp();
    csp.scriptSrcSha({hash: 'AbC', algo: 'sha384'});
    assert.assertEquals("script-src 'sha384-AbC'", __.toNativeObject(testInstance.policyBuild()));
};

exports.styleSrcShaContent = function () {
    var csp = portal.csp();
    csp.styleSrcSha({content: 'body { color: red; }'});
    assert.assertEquals(
        "style-src 'sha256-" + testInstance.shaBase64('body { color: red; }', 'SHA-256') + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.granularDirectives = function () {
    var csp = portal.csp();
    csp.scriptSrcElem(portal.CspSource.SELF);
    csp.scriptSrcAttr(portal.CspSource.NONE);
    csp.styleSrcElem(portal.CspSource.SELF);
    csp.styleSrcAttr(portal.CspSource.NONE);
    assert.assertEquals(
        "script-src-attr 'none'; script-src-elem 'self'; style-src-attr 'none'; style-src-elem 'self'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.reportToDirective = function () {
    var csp = portal.csp();
    csp.reportTo('csp-endpoint');
    assert.assertEquals('report-to csp-endpoint', __.toNativeObject(testInstance.policyBuild()));
};

exports.trustedTypesDirectives = function () {
    var csp = portal.csp();
    csp.requireTrustedTypesFor();
    csp.trustedTypes('my-policy', "'allow-duplicates'");
    assert.assertEquals(
        "require-trusted-types-for 'script'; trusted-types my-policy 'allow-duplicates'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.reportOnlyFlag = function () {
    var csp = portal.csp();
    assert.assertTrue('default not report-only', !__.toNativeObject(testInstance.policyReportOnly()));
    csp.reportOnly(true);
    assert.assertTrue('report-only after enable', __.toNativeObject(testInstance.policyReportOnly()));
};

exports.cspSourceTokens = function () {
    assert.assertEquals("'self'", portal.CspSource.SELF);
    assert.assertEquals("'none'", portal.CspSource.NONE);
    assert.assertEquals("'unsafe-inline'", portal.CspSource.UNSAFE_INLINE);
    assert.assertEquals("'unsafe-eval'", portal.CspSource.UNSAFE_EVAL);
    assert.assertEquals("'strict-dynamic'", portal.CspSource.STRICT_DYNAMIC);
    assert.assertEquals("'unsafe-hashes'", portal.CspSource.UNSAFE_HASHES);
    assert.assertEquals("'wasm-unsafe-eval'", portal.CspSource.WASM_UNSAFE_EVAL);
    assert.assertEquals("'report-sample'", portal.CspSource.REPORT_SAMPLE);
    assert.assertEquals('data:', portal.CspSource.DATA);
    assert.assertEquals('blob:', portal.CspSource.BLOB);
};

exports.schemeSourcesTyped = function () {
    var csp = portal.csp();
    csp.imgSrc(portal.CspSource.SELF, portal.CspSource.DATA, portal.CspSource.BLOB);
    assert.assertEquals("img-src 'self' data: blob:", __.toNativeObject(testInstance.policyBuild()));
};

exports.sandboxFlagTokens = function () {
    assert.assertEquals('allow-scripts', portal.SandboxFlag.ALLOW_SCRIPTS);
    assert.assertEquals('allow-same-origin', portal.SandboxFlag.ALLOW_SAME_ORIGIN);
    assert.assertEquals('allow-forms', portal.SandboxFlag.ALLOW_FORMS);
    assert.assertEquals('allow-popups', portal.SandboxFlag.ALLOW_POPUPS);
    assert.assertEquals('allow-modals', portal.SandboxFlag.ALLOW_MODALS);
    assert.assertEquals('allow-top-navigation', portal.SandboxFlag.ALLOW_TOP_NAVIGATION);
    assert.assertEquals('allow-downloads', portal.SandboxFlag.ALLOW_DOWNLOADS);
    assert.assertEquals('allow-pointer-lock', portal.SandboxFlag.ALLOW_POINTER_LOCK);
    assert.assertEquals('allow-presentation', portal.SandboxFlag.ALLOW_PRESENTATION);
    assert.assertEquals('allow-orientation-lock', portal.SandboxFlag.ALLOW_ORIENTATION_LOCK);
};

exports.restrictiveDirectivesTyped = function () {
    var csp = portal.csp();
    csp.frameAncestors(portal.CspSource.NONE);
    csp.baseUri(portal.CspSource.SELF);
    csp.formAction(portal.CspSource.SELF);
    assert.assertEquals(
        "base-uri 'self'; form-action 'self'; frame-ancestors 'none'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.strict = function () {
    var csp = portal.csp();
    csp.strict();
    assert.assertEquals(
        "base-uri 'none'; default-src 'none'; frame-ancestors 'none'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.strictThenOpenUp = function () {
    var csp = portal.csp();
    csp.strict().scriptSrc(portal.CspSource.SELF).styleSrc(portal.CspSource.SELF);
    assert.assertEquals(
        "base-uri 'none'; default-src 'none'; frame-ancestors 'none'; script-src 'self'; style-src 'self'",
        __.toNativeObject(testInstance.policyBuild())
    );
};
