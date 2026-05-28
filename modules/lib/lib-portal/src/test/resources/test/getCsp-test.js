var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.returnsObject = function () {
    var csp = portal.getCsp();
    assert.assertNotNull(csp);
    assert.assertEquals('function', typeof csp.add);
    assert.assertEquals('function', typeof csp.set);
    assert.assertEquals('function', typeof csp.addSha);
    assert.assertEquals('function', typeof csp.getNonce);
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
