/**
 * Mail related functions.
 *
 * @example
 * var authLib = require('/lib/xp/mail');
 *
 * @module lib/xp/mail
 */

function checkRequired(params, name) {
    if (params[name] === undefined) {
        throw "Parameter '" + name + "' is required";
    }
}

exports.send = function (message) {

    var bean = __.newBean('com.enonic.xp.lib.mail.SendMailHandler');

    checkRequired(message, 'from');
    checkRequired(message, 'to');

    bean.subject = __.nullOrValue(message.subject);
    bean.from = [].concat(__.nullOrValue(message.from));
    bean.to = [].concat(__.nullOrValue(message.to));
    bean.cc = [].concat(__.nullOrValue(message.cc));
    bean.bcc = [].concat(__.nullOrValue(message.bcc));
    bean.replyTo = [].concat(__.nullOrValue(message.replyTo));
    bean.body = __.nullOrValue(message.body);
    bean.contentType = __.nullOrValue(message.contentType);
    bean.headers = __.nullOrValue(message.headers);

    return bean.send();

};
