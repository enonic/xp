var contentLib = require('/lib/xp/content');

var TestClass = Java.type('com.enonic.xp.lib.content.AddAttachmentHandlerTest');
var dataStream = TestClass.createByteSource('image data');

// BEGIN
// Removes an attachment.
contentLib.addAttachment({
    key: '/mySite/mycontent',
    name: 'image',
    mimeType: 'image/png',
    label: 'photo',
    data: dataStream
});
// END
