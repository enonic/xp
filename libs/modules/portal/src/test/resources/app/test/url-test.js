var assert = Java.type('org.junit.Assert');
var portal = require('/lib/xp/portal.js');

exports.assetUrlTest = function () {
    var result = portal.assetUrl({
        path: 'styles/my.css',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('AssetUrlParams{params={a=[1], b=[1, 2]}, path=styles/my.css}', result);
    return true;
};

exports.attachmentUrlTest = function () {
    var result = portal.attachmentUrl({
        name: "myattachment.pdf",
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('AttachmentUrlParams{params={a=[1], b=[1, 2]}, name=myattachment.pdf, download=false}', result);
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

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ComponentUrlParams{params={a=[1], b=[1, 2]}, component=mycomp}', result);
    return true;
};

exports.imageUrlTest = function () {
    var result = portal.imageUrl({
        id: '123',
        background: 'ffffff',
        quality: 90,
        filter: 'scale(1,1)',
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ImageUrlParams{params={a=[1], b=[1, 2]}, id=123, quality=90, filter=scale(1,1), background=ffffff}', result);
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

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('PageUrlParams{params={a=[1], b=[1, 2]}, path=a/b}', result);
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

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ServiceUrlParams{params={a=[1], b=[1, 2]}, service=myservice}', result);
    return true;
};

exports.processHtmlTest = function () {
    var result = portal.processHtml({
        value: '<p><a title="Link tooltip" href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>'
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ProcessHtmlParams{params={}, value=<p><a title="Link tooltip" ' +
                        'href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>}', result);
    return true;
};
