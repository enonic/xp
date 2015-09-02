var assert = require('/lib/xp/assert.js');
var mail = require('/lib/xp/mail.js');

exports.simpleMail = function () {

    var result = mail.send({
        subject: 'test',
        to: 'foo@bar.com',
        from: 'foo@bar.com',
        headers: {
            'X-Custom': 'Value',
            'X-Other': '2'
        }
    });

    assert.assertEquals('Should be true', true, result);

};
