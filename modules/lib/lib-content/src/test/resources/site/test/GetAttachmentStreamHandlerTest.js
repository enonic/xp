var t = require('/lib/xp/testing');
var content = require('/lib/xp/content');

exports.getAttachmentStreamById = function () {
    var result = content.getAttachmentStream({
        key: '123456',
        name: 'document.pdf'
    });

    t.assertNotNull(result);
};

exports.getAttachmentStreamByPath = function () {
    var result = content.getAttachmentStream({
        key: '/a/b/mycontent',
        name: 'document.pdf'
    });

    t.assertNotNull(result);
};

exports.getAttachmentStreamById_notFound = function () {
    var result = content.getAttachmentStream({
        key: '123456',
        name: 'document.pdf'
    });

    t.assertNull(result);
};

exports.getAttachmentStreamByPath_notFound = function () {
    var result = content.getAttachmentStream({
        key: '/a/b/mycontent',
        name: 'document.pdf'
    });

    t.assertNull(result);
};

exports.getAttachmentStreamById_AttachmentNotFound = function () {
    var result = content.getAttachmentStream({
        key: '123456',
        name: 'other.pdf'
    });

    t.assertNull(result);
};