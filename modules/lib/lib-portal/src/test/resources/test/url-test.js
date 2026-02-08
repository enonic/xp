var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.assetUrlTest = function () {
    var result = portal.assetUrl({
        path: 'styles/my.css',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('/site/mocksite/_/asset/styles/my.css') === 0);
    return true;
};

exports.assetUrlTest_unknownProperty = function () {
    var result = portal.assetUrl({
        path: 'styles/my.css',
        unknownProperty: 'value',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertTrue(result.indexOf('/site/mocksite/_/asset/styles/my.css') === 0);
    return true;
};

exports.assetUrlTest_invalidProperty = function () {
    try {
        portal.assetUrl({
            _path: 'styles/my.css',
            params: {
                a: 1,
                b: [1, 2]
            }
        });
    } catch (e) {
        assert.assertEquals('Parameter \'path\' is required', e.message);
    }
    return true;
};

exports.attachmentUrlTest = function () {
    var result = portal.attachmentUrl({
        name: 'myattachment.pdf',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('/site/mocksite/_/attachment/inline/mockid/myattachment.pdf') === 0);
    return true;
};

exports.attachmentUrlTest_unknownProperty = function () {
    var result = portal.attachmentUrl({
        name: 'myattachment.pdf',
        unknownProperty: 'value',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertTrue(result.indexOf('/site/mocksite/_/attachment/inline/mockid/myattachment.pdf') === 0);
    return true;
};

exports.componentUrlTest = function () {
    var result = portal.componentUrl({
        component: 'mycomp',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('/site/mocksite/_/component/mycomp') === 0);
    return true;
};

exports.componentUrlTest_unknownProperty = function () {
    var result = portal.componentUrl({
        component: 'mycomp',
        unknownProperty: 'value',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertTrue(result.indexOf('/site/mocksite/_/component/mycomp') === 0);
    return true;
};

exports.imageUrlTest = function () {
    var result = portal.imageUrl({
        id: '123',
        background: 'ffffff',
        quality: 90,
        scale: 'block(200,100)',
        filter: 'scale(1,1)',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('/site/mocksite/_/image/123') === 0);
    return true;
};

exports.imageUrlTest_unknownProperty = function () {
    var result = portal.imageUrl({
        id: '123',
        background: 'ffffff',
        quality: 90,
        scale: 'block(200,100)',
        filter: 'scale(1,1)',
        params: {
            a: 1,
            b: [1, 2]
        },
        unknownProperty: 'value'
    });

    assert.assertTrue(result.indexOf('/site/mocksite/_/image/123') === 0);
    return true;
};

exports.pageUrlTest = function () {
    var result = portal.pageUrl({
        path: 'a/b',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('/site/mocksite/a/b') === 0);
    return true;
};

exports.pageUrlTest_unknownProperty = function () {
    var result = portal.pageUrl({
        path: 'a/b',
        params: {
            a: 1,
            b: [1, 2]
        },
        unknownProperty: 'value'
    });

    assert.assertTrue(result.indexOf('/site/mocksite/a/b') === 0);
    return true;
};

exports.serviceUrlTest = function () {
    var result = portal.serviceUrl({
        service: 'myservice',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('/site/mocksite/_/service/myservice') === 0);
    return true;
};

exports.serviceUrlWebSocketTest = function () {
    var result = portal.serviceUrl({
        service: 'myservice',
        type: 'websocket',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // Verify the result is a proper mock URL
    assert.assertTrue(result.indexOf('ws://myservice') === 0);
    return true;
};

exports.serviceUrlTest_unknownProperty = function () {
    var result = portal.serviceUrl({
        service: 'myservice',
        unknownProperty: 'value',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertTrue(result.indexOf('/site/mocksite/_/service/myservice') === 0);
    return true;
};

exports.processHtmlTest = function () {
    var result = portal.processHtml({
        value: '<p><a title="Link tooltip" href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>'
    });

    // Verify the result is the processed HTML value
    assert.assertEquals('<p><a title="Link tooltip" href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>', result);
    return true;
};

exports.processHtmlTest_ignoreUnknownProperty = function () {
    var result = portal.processHtml({
        value: '<p><a title="Link tooltip" href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>',
        unknownProperty: 'value'
    });

    assert.assertEquals('<p><a title="Link tooltip" href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>', result);
    return true;
};

exports.processHtmlImageUrlProcessingTest = function() {
    var result = portal.processHtml({
        value: '<p><figure class="editor-align-justify">' +
                       '<img alt="Alt text" src="image://3e266eea-9875-4cb7-b259-41ad152f8532"/>' +
                       '<figcaption>Caption text</figcaption></figure></p>',
        imageWidths: [660, 1024],
        imageSizes: '(max-width: 960px) 600px'
    });

    assert.assertEquals('<p><figure class="editor-align-justify"><img alt="Alt text" src="image://3e266eea-9875-4cb7-b259-41ad152f8532"/><figcaption>Caption text</figcaption></figure></p>', result);

    return true;
};

exports.imagePlaceholderTest = function () {
    var result = portal.imagePlaceholder({
        width: 32,
        height: 10
    });

    assert.assertEquals(
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAKCAYAAADVTVykAAAAFUlEQVR4nGNgGAWjYBSMglEwCkgHAAZAAAEAAhBgAAAAAElFTkSuQmCC',
        result);
    return true;
};
