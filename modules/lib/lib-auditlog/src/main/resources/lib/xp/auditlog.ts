/**
 * Functions to log and find audit logs.
 *
 * @example
 * var auditLib = require('/lib/xp/auditlog');
 *
 * @module audit
 */

declare global {
    interface XpLibraries {
        '/lib/xp/auditlog': typeof import('./auditlog');
    }
}

import type {ScriptValue, UserKey} from '@enonic-types/core';

export type {ScriptValue, UserKey} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export interface AuditLogParams<Data extends Record<string, unknown>> {
    type: string;
    time?: string;
    source?: string;
    user?: UserKey;
    objects?: string[];
    data?: Data;
}

export interface AuditLog<Data extends Record<string, unknown> = Record<string, unknown>> {
    _id: string;
    type: string;
    time: string;
    source: string;
    user: UserKey;
    objects: string[];
    data: Data;
}

interface CreateAuditLogHandler<Data extends Record<string, unknown>> {
    setType(type: string): void;

    setTime(type?: string | null): void;

    setSource(source: string): void;

    setUser(user?: string | null): void;

    setObjectUris(objectUris?: ScriptValue): void;

    setData(data?: ScriptValue): void;

    execute(): AuditLog<Data>;
}

/**
 * This function creates a single audit log entry.
 *
 * The parameter 'type' is required and all other parameters are optional.
 *
 * @example-ref examples/auditlog/log.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.type Type of log entry.
 * @param {string} [params.time] Log entry timestamp. Defaults to now.
 * @param {string} [params.source] Log entry source. Defaults to the application ID.
 * @param {string} [params.user] Log entry user. Defaults to the user of current context.
 * @param {array}  [params.objects] URIs to objects that relate to this log entry. Defaults to empty array.
 * @param {object} [params.data] Custom extra data for this log entry. Defaults to empty object.
 *
 * @returns {object} Audit log created as JSON.
 */
export function log<Data extends Record<string, unknown> = Record<string, unknown>>(params: AuditLogParams<Data>): AuditLog<Data> {
    checkRequired(params, 'type');

    const bean = __.newBean<CreateAuditLogHandler<Data>>('com.enonic.xp.lib.audit.CreateAuditLogHandler');
    bean.setType(params.type);
    bean.setTime(__.nullOrValue(params.time));
    bean.setSource(params.source ?? app.name);
    bean.setUser(__.nullOrValue(params.user));
    bean.setObjectUris(__.toScriptValue(params.objects));
    bean.setData(__.toScriptValue(params.data));

    return __.toNativeObject(bean.execute());
}

export interface GetAuditLogParams {
    id: string;
}

interface GetAuditLogHandler {
    setId(id: string): void;

    execute(): AuditLog | null;
}

/**
 * This function fetches an audit log.
 *
 * @example-ref examples/auditlog/get.js
 *
 * @param {object} params     JSON with the parameters.
 * @param {string} params.id  Id of the audit log.
 *
 * @returns {object} Audit log as JSON.
 */
export function get(params: GetAuditLogParams): AuditLog | null {
    checkRequired(params, 'id');

    const bean = __.newBean<GetAuditLogHandler>('com.enonic.xp.lib.audit.GetAuditLogHandler');
    bean.setId(params.id);
    return __.toNativeObject(bean.execute());
}

export interface FindAuditLogParams {
    start?: number;
    count?: number;
    ids?: string[];
    from?: string;
    to?: string;
    type?: string;
    source?: string;
    users?: string[];
    objects?: string[];
}

export interface AuditLogs {
    total: number;
    count: number;
    hits: AuditLog[];
}

interface FindAuditLogHandler {
    setStart(start: number): void;

    setCount(count: number): void;

    setIds(ids?: ScriptValue): void;

    setFrom(from?: string | null): void;

    setTo(to?: string | null): void;

    setType(type?: string | null): void;

    setSource(source?: string | null): void;

    setUsers(users?: ScriptValue): void;

    setObjectUris(objectUris?: ScriptValue): void;

    execute(): AuditLogs;
}

/**
 * This function searches for audit logs.
 *
 * All parameters are semi-optional, meaning that you should at least supply one
 * of them. If no parameters are supplied you will get an empty result.
 *
 * @example-ref examples/auditlog/find.js
 *
 * @param {object} params     JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {array} [params.ids] Filter by ids of audit logs.
 * @param {string} [params.from] Filter by logs younger than from.
 * @param {string} [params.to] Filter by logs older than to.
 * @param {string} [params.type] Filter by type.
 * @param {string} [params.source] Filter by source.
 * @param {array} [params.users] Filter by user keys.
 * @param {array} [params.objects] Filter by object URIs.
 *
 * @returns {object} Audit log search results.
 */
export function find(params: FindAuditLogParams): AuditLogs {
    const {
        start = 0,
        count = 10,
        ids,
        from,
        to,
        type,
        source,
        users,
        objects,
    } = params ?? {};

    const bean = __.newBean<FindAuditLogHandler>('com.enonic.xp.lib.audit.FindAuditLogHandler');
    bean.setStart(start);
    bean.setCount(count);
    bean.setIds(__.toScriptValue(ids));
    bean.setFrom(__.nullOrValue(from));
    bean.setTo(__.nullOrValue(to));
    bean.setType(__.nullOrValue(type));
    bean.setSource(__.nullOrValue(source));
    bean.setUsers(__.toScriptValue(users));
    bean.setObjectUris(__.toScriptValue(objects));
    return __.toNativeObject(bean.execute());
}
