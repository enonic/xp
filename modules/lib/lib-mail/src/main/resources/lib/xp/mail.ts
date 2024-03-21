declare global {
    interface XpLibraries {
        '/lib/xp/mail': typeof import('./mail');
    }
}

import type {ByteSource} from '@enonic-types/core';

export type {ByteSource} from '@enonic-types/core';

/**
 * Mail related functions.
 *
 * @example
 * var mailLib = require('/lib/xp/mail');
 *
 * @module mail
 */

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

interface SendMailHandler {
    setSubject(value?: string | null): void;

    setFrom(value: string[]): void;

    setTo(value: string[]): void;

    setCc(value: string[]): void;

    setBcc(value: string[]): void;

    setReplyTo(value: string[]): void;

    setBody(value?: string | null): void;

    setContentType(contentType?: string | null): void;

    setHeaders(headers?: Record<string, string> | null): void;

    setAttachments(attachments?: Attachment[] | null): void;

    send(): boolean;
}

export interface Attachment {
    fileName: string;
    data: ByteSource;
    mimeType?: string;
    headers?: Record<string, string>;
}

export interface SendMessageParams {
    subject: string;
    from: string;
    body: string;
    to: string | string[];
    cc?: string | string[];
    bcc?: string | string[];
    replyTo?: string;
    contentType?: string;
    headers?: Record<string, string>;
    attachments?: Attachment[];
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
 * @param {object} params JSON with the parameters.
 * @param {string} params.from The email address, and optionally name of the sender of the message.
 * @param {(string|string[])} params.to The email address(es), and optionally name(s) of the primary message’s recipient(s).
 * @param {(string|string[])} [params.cc] The carbon copy email address(es).
 * @param {(string|string[])} [params.bcc] The blind carbon copy email address(es).
 * @param {string} [params.replyTo] The email address that should be used to reply to the message.
 * @param {string} params.subject The subject line of the message.
 * @param {string} params.body The text content of the message.
 * @param {string} [params.contentType] Content type of the message body.
 * @param {object} [params.headers] Custom headers in the form of name-value pairs.
 * @param {object[]} [params.attachments] Attachments to include in the email.
 * @param {string} params.attachments.fileName Attachment file name.
 * @param {*} params.attachments.data Attachment stream.
 * @param {string} [params.attachments.mimeType] Attachment content type. If not specified will be inferred from the file extension.
 * @param {object} [params.attachments.headers] Attachment headers, in the form of name-value pairs.
 *
 * @returns {boolean} True if the message was sent successfully, false otherwise.
 */
export function send(params: SendMessageParams): boolean {
    const bean = __.newBean<SendMailHandler>('com.enonic.xp.lib.mail.SendMailHandler');

    checkRequired(params, 'from');
    checkRequired(params, 'to');

    const {
        subject,
        body,
        from = [],
        to = [],
        cc = [],
        bcc = [],
        replyTo = [],
        contentType,
        headers,
        attachments,
    } = params ?? {};

    bean.setSubject(__.nullOrValue(subject));
    bean.setFrom(([] as string[]).concat(from));
    bean.setTo(([] as string[]).concat(to));
    bean.setCc(([] as string[]).concat(cc));
    bean.setBcc(([] as string[]).concat(bcc));
    bean.setReplyTo(([] as string[]).concat(replyTo));
    bean.setBody(__.nullOrValue(body));
    bean.setContentType(__.nullOrValue(contentType));
    bean.setHeaders(__.nullOrValue(headers));
    bean.setAttachments(__.nullOrValue(attachments));

    return bean.send();
}


interface GetDefaultFromEmailHandler {
    execute(): string | null;
}

/**
 * This function returns defaultFromMail.
 *
 * @returns {string} The default from mail address, or null if not set.
 */
export function getDefaultFromEmail(): string | null {
    const bean = __.newBean<GetDefaultFromEmailHandler>('com.enonic.xp.lib.mail.GetDefaultFromEmailHandler');
    return bean.execute();
}
