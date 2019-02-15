var contentLib = require('/lib/xp/content');

// BEGIN
// Removes an attachment, by content path.
contentLib.removeAttachment({key: '/mySite/mycontent', name: 'document'});
// END

// BEGIN
// Removes multiple attachments, by content id.
contentLib.removeAttachment({key: '3381d720-993e-4576-b089-aaf67280a74c', name: ['document', 'image']});
// END
