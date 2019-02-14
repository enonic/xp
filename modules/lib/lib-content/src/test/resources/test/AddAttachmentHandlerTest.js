var content = require('/lib/xp/content.js');

var TestClass = Java.type('com.enonic.xp.lib.content.AddAttachmentHandlerTest');
var dataStream = TestClass.createByteSource('image data');

exports.addAttachmentById = function () {
    content.addAttachment({
        key: '3381d720-993e-4576-b089-aaf67280a74c',
        name: 'image',
        mimeType: 'image/png',
        label: 'photo',
        data: dataStream
    });
};

exports.addAttachmentWithString = function () {
    content.addAttachment({
        key: '3381d720-993e-4576-b089-aaf67280a74c',
        name: 'image',
        mimeType: 'image/png',
        label: 'photo',
        data: 'text'
    });
};


exports.addAttachmentWithObject = function () {
    content.addAttachment({
        key: '3381d720-993e-4576-b089-aaf67280a74c',
        name: 'image',
        mimeType: 'image/png',
        label: 'photo',
        data: {}
    });
};
