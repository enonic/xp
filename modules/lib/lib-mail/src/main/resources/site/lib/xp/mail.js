/**
 * Mail related functions.
 *
 * @example
 * var mailLib = require('/lib/xp/mail');
 *
 * @module mail
 */

function checkRequired(params, name) {
    if (params[name] === undefined) {
        throw "Parameter '" + name + "' is required";
    }
}

/**
 * This function sends an email message using the mail server configured.
 *
 * The address values can be either a simple email address (e.g. ‘name@domain.org’ ) or an address with a
 * display name. In the latter case the email will be enclosed with angle brackets (e.g. ‘Some Name <name@domain.org>’ ).
 *
 * The parameters `to`, `cc` and `bcc` can be passed as a single string or as an array of strings, if there are multiple addresses
 * to specify.
 *
 * The content-type of the email can be specified by using the `contentType` parameter. See example below for
 * sending a message with an HTML body.
 *
 * @example-ref examples/mail/send.js
 *
 * @param {object} message JSON with the parameters.
 * @param {string} message.from The email address, and optionally name of the sender of the message.
 * @param {(string|string[])} message.to The email address(es), and optionally name(s) of the primary message’s recipient(s).
 * @param {(string|string[])} [message.cc] The carbon copy email address(es).
 * @param {(string|string[])} [message.bcc] The blind carbon copy email address(es).
 * @param {string} [message.replyTo] The email address that should be used to reply to the message.
 * @param {string} message.subject The subject line of the message.
 * @param {string} message.body The text content of the message.
 * @param {string} [message.contentType] Content type of the message body.
 * @param {object} [message.headers] Custom headers in the form of name-value pairs.
 * @param {object[]} [message.attachments] Attachments to include in the email.
 * @param {string} message.attachments.fileName Attachment file name.
 * @param {*} message.attachments.data Attachment stream.
 * @param {string} [message.attachments.mimeType] Attachment content type. If not specified will be inferred from the file extension.
 * @param {object} [message.attachments.headers] Attachment headers, in the form of name-value pairs.
 *
 * @returns {boolean} True if the message was sent successfully, false otherwise.
 */
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
    bean.attachments = __.nullOrValue(message.attachments);

    return bean.send();

};
