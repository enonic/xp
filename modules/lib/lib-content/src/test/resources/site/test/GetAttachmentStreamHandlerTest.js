var assert = require('/lib/xp/assert.js');
var content = require('/lib/xp/content.js');

exports.getAttachmentStreamById = function () {
    var result = content.getAttachmentStream({
        key: '123456',
        name: 'document.pdf'
    });

    assert.assertNotNull(result);
};

exports.getAttachmentStreamByPath = function () {
    var result = content.getAttachmentStream({
        key: '/a/b/mycontent',
        name: 'document.pdf'
    });

    assert.assertNotNull(result);
};

exports.getAttachmentStreamById_notFound = function () {
    var result = content.getAttachmentStream({
        key: '123456',
        name: 'document.pdf'
    });

    assert.assertNull(result);
};

exports.getAttachmentStreamByPath_notFound = function () {
    var result = content.getAttachmentStream({
        key: '/a/b/mycontent',
        name: 'document.pdf'
    });

    assert.assertNull(result);
};

exports.getAttachmentStreamById_AttachmentNotFound = function () {
    var result = content.getAttachmentStream({
        key: '123456',
        name: 'other.pdf'
    });

    assert.assertNull(result);
};