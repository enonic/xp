var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.returnsObject = function () {
    var csp = portal.getCsp();
    assert.assertNotNull(csp);
    assert.assertEquals('function', typeof csp.add);
    assert.assertEquals('function', typeof csp.set);
    assert.assertEquals('function', typeof csp.addSha);
    assert.assertEquals('function', typeof csp.getNonce);
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
    assert.assertEquals('function', typeof csp.sandbox);
    assert.assertEquals('function', typeof csp.addScriptSrcSha);
    assert.assertEquals('function', typeof csp.addStyleSrcSha);
};

exports.addSources = function () {
    var csp = portal.getCsp();
    csp.add('script-src', ["'self'", 'https://cdn.example.com']);
    csp.add('script-src', ['https://cdn.example.com', "'unsafe-inline'"]);
    assert.assertEquals("script-src 'self' https://cdn.example.com 'unsafe-inline'", __.toNativeObject(testInstance.policyBuild()));
};

exports.setResets = function () {
    var csp = portal.getCsp();
    csp.add('style-src', ["'self'"]);
    csp.set('style-src', ["'none'"]);
    assert.assertEquals("style-src 'none'", __.toNativeObject(testInstance.policyBuild()));
};

exports.addAfterSet = function () {
    var csp = portal.getCsp();
    csp.set('img-src', ["'self'"]);
    csp.add('img-src', ['data:']);
    assert.assertEquals("img-src 'self' data:", __.toNativeObject(testInstance.policyBuild()));
};

exports.addShaContent = function () {
    var csp = portal.getCsp();
    csp.addSha('script-src', 'window.foo = 42;');
    // SHA-256 of 'window.foo = 42;' as UTF-8 bytes, base64.
    assert.assertEquals(
        "script-src 'sha256-" + testInstance.sha256Base64('window.foo = 42;') + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.addShaDigest = function () {
    var csp = portal.getCsp();
    csp.addSha('script-src', 'AbCdEf', 'sha384');
    assert.assertEquals("script-src 'sha384-AbCdEf'", __.toNativeObject(testInstance.policyBuild()));
};

exports.unsupportedAlgo = function () {
    var csp = portal.getCsp();
    var threw = false;
    try {
        csp.addSha('script-src', 'AbCd', 'md5');
    } catch (e) {
        threw = true;
    }
    assert.assertTrue('expected addSha to throw on unsupported algo', threw);
};

exports.nonceLazyAndStable = function () {
    var csp = portal.getCsp();
    var n1 = csp.getNonce();
    var n2 = csp.getNonce();
    assert.assertEquals(n1, n2);
    // Default applies to script-src.
    assert.assertEquals("script-src 'nonce-" + n1 + "'", __.toNativeObject(testInstance.policyBuild()));
};

exports.manualNonceInStyleSrc = function () {
    var csp = portal.getCsp();
    var n = csp.getNonce();
    csp.add('style-src', ["'nonce-" + n + "'"]);
    assert.assertEquals(
        "script-src 'nonce-" + n + "'; style-src 'nonce-" + n + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.scriptSrcTypedAndRaw = function () {
    var csp = portal.getCsp();
    csp.scriptSrc(portal.CspSource.SELF, 'https://cdn.example.com');
    assert.assertEquals(
        "script-src 'self' https://cdn.example.com",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.scriptSrcAndAddUnion = function () {
    var csp = portal.getCsp();
    csp.scriptSrc(portal.CspSource.SELF);
    csp.add('script-src', ['https:']);
    csp.scriptSrc(portal.CspSource.SELF);
    assert.assertEquals(
        "script-src 'self' https:",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.upgradeInsecureRequests = function () {
    var csp = portal.getCsp();
    csp.upgradeInsecureRequests();
    assert.assertEquals('upgrade-insecure-requests', __.toNativeObject(testInstance.policyBuild()));
};

exports.sandboxSingleFlag = function () {
    var csp = portal.getCsp();
    csp.sandbox(portal.SandboxFlag.ALLOW_SCRIPTS);
    assert.assertEquals('sandbox allow-scripts', __.toNativeObject(testInstance.policyBuild()));
};

exports.sandboxMultipleFlags = function () {
    var csp = portal.getCsp();
    csp.sandbox(portal.SandboxFlag.ALLOW_SCRIPTS, portal.SandboxFlag.ALLOW_SAME_ORIGIN);
    assert.assertEquals('sandbox allow-scripts allow-same-origin', __.toNativeObject(testInstance.policyBuild()));
};

exports.addScriptSrcShaContent = function () {
    var csp = portal.getCsp();
    csp.addScriptSrcSha('window.foo = 42;');
    assert.assertEquals(
        "script-src 'sha256-" + testInstance.sha256Base64('window.foo = 42;') + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
};

exports.addScriptSrcShaDigest = function () {
    var csp = portal.getCsp();
    csp.addScriptSrcSha('AbC', 'sha384');
    assert.assertEquals("script-src 'sha384-AbC'", __.toNativeObject(testInstance.policyBuild()));
};

exports.addStyleSrcShaContent = function () {
    var csp = portal.getCsp();
    csp.addStyleSrcSha('body { color: red; }');
    assert.assertEquals(
        "style-src 'sha256-" + testInstance.sha256Base64('body { color: red; }') + "'",
        __.toNativeObject(testInstance.policyBuild())
    );
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
    var csp = portal.getCsp();
    csp.frameAncestors(portal.CspSource.NONE);
    csp.baseUri(portal.CspSource.SELF);
    csp.formAction(portal.CspSource.SELF);
    assert.assertEquals(
        "base-uri 'self'; form-action 'self'; frame-ancestors 'none'",
        __.toNativeObject(testInstance.policyBuild())
    );
};
