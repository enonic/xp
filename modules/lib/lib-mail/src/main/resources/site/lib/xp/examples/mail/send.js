var mailLib = require('/lib/xp/mail.js');
var assert = require('/lib/xp/testing.js');

var stream1 = testInstance.createByteSource('image data');
var stream2 = testInstance.createByteSource('some text');

// BEGIN
// Send a simple HTML mail.
var flag1 = mailLib.send({
    from: 'me@enonic.com',
    to: 'user@somewhere.org',
    subject: 'HTML email test',
    body: '<h1>HTML Email!</h1><p>You can use the contentType parameter for HTML messages.</p>',
    contentType: 'text/html; charset="UTF-8"'
});
// END

// BEGIN
// Send a mail with attachments.
var flag2 = mailLib.send({
    from: 'Sales Department <sales@enonic.com>',
    to: 'user@somewhere.org',
    subject: 'Email Test from Enonic XP',
    cc: 'other@example.org',
    bcc: ['support@enonic.com', 'other@enonic.com'],
    replyTo: 'support@enonic.com',
    body: 'Welcome to Enonic XP!' + '\r\n\r\n' + '- The Dev Team',
    headers: {
        'Disposition-Notification-To': 'me@enonic.com'
    },
    attachments: [
        {
            fileName: 'logo.png',
            mimeType: 'image/png',
            data: stream1,
            headers: {
                'Content-ID': 'logo-img'
            }
        },
        {
            fileName: 'text.txt',
            data: stream2
        }
    ]
});
// END

assert.assertTrue(flag1);
assert.assertTrue(flag2);
