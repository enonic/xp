var assert = require('/lib/xp/assert.js');
var mail = require('/lib/xp/mail.js');

var TestClass = Java.type('com.enonic.xp.lib.mail.SendMailScriptTest');

exports.simpleMail = function () {

    var result = mail.send({
        subject: 'test subject',
        body: 'test body',
        to: 'to@bar.com',
        from: 'from@bar.com',
        cc: 'cc@bar.com',
        bcc: 'bcc@bar.com',
        replyTo: 'replyTo@bar.com',
        headers: {
            'X-Custom': 'Value',
            'X-Other': '2'
        }
    });

    assert.assertEquals('Should be true', true, result);

};

exports.multiRecipientsMail = function () {

    var result = mail.send({
        subject: 'test subject',
        body: 'test body',
        to: ['to@bar.com', 'to@foo.com'],
        from: ['from@bar.com', 'from@foo.com'],
        cc: ['cc@bar.com', 'cc@foo.com'],
        bcc: ['bcc@bar.com', 'bcc@foo.com'],
        replyTo: ['replyTo@bar.com', 'replyTo@foo.com']
    });

    assert.assertEquals('Should be true', true, result);

};

exports.rfc822AddressMail = function () {

    var result = mail.send({
        subject: 'test subject',
        body: 'test body',
        to: ['To Bar <to@bar.com>', 'To Foo <to@foo.com>'],
        from: ['From Bar <from@bar.com>', 'From Foo <from@foo.com>']
    });

    assert.assertEquals('Should be true', true, result);

};

exports.failSendMail = function () {

    var result = mail.send({
        subject: 'test subject',
        body: 'test body',
        to: 'to@@@bar.com',
        from: 'from@bar.com'
    });

    assert.assertEquals('Should be false', false, result);

};

exports.sendMailWithContentType = function () {

    var result = mail.send({
        subject: 'test subject',
        body: 'test body',
        to: 'to@bar.com',
        from: 'from@bar.com',
        contentType: 'text/html'
    });

    assert.assertEquals('Should be true', true, result);

};

exports.sendWithoutRequiredFrom = function () {

    var result = mail.send({
        subject: 'test subject',
        to: 'to@bar.com'
    });

};

exports.sendWithoutRequiredTo = function () {

    var result = mail.send({
        subject: 'test subject',
        from: 'to@bar.com'
    });

};

exports.sendWithAttachments = function () {

    var result = mail.send({
        subject: 'test subject',
        body: 'test body',
        to: 'to@bar.com',
        from: 'from@bar.com',
        attachments: [
            {
                fileName: 'image.png',
                mimeType: 'image/png',
                data: TestClass.createByteSource('image data')
            },
            {
                fileName: 'text.txt',
                data: TestClass.createByteSource('Some text')
            }
        ]
    });

};