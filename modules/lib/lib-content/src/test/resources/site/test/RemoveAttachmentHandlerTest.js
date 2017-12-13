var content = require('/lib/xp/content.js');

exports.removeAttachmentSingle = function () {
    content.removeAttachment({
        key: '3381d720-993e-4576-b089-aaf67280a74c',
        name: 'image'
    });
};

exports.removeAttachmentMulti = function () {
    content.removeAttachment({
        key: '/mysite/mypath',
        name: ['image', 'docs']
    });
};
