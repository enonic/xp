declare global {
    interface XpLibraries {
        '/lib/xp/auditlog': typeof import('./auditlog');
    }
}

/**
 * Functions to log and find audit logs.
 *
 * @example
 * var auditLib = require('/lib/xp/auditlog');
 *
 * @module audit
 */

function required<T extends Object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw `Parameter \'${String(name)}\' is required`;
    }
}

export interface AuditLogParams {
    type: string;
    time?: string;
    source?: string;
    user?: string;
    objects?: string[];
    data?: Record<string, unknown>;
}

export interface AuditLog {
    _id: string;
    type: string;
    time: string;
    source: string;
    user: string;
    objects: string[];
    data: Record<string, unknown>;
}

interface CreateAuditLogHandler {
    setType(type: string): void;

    setTime(type?: string | null): void;

    setSource(source: string): void;

    setUser(user?: string | null): void;

    setObjectUris(objectUris?: string[] | null): void;

    setData(data?: Record<string, unknown> | null): void;

    execute(): AuditLog;
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
 * @param {object} [params.data] Custom extra data for the this log entry. Defaults to empty object.
 *
 * @returns {object} Audit log created as JSON.
 */
export function log(params: AuditLogParams): AuditLog {
    required(params, 'type');

    const bean = __.newBean<CreateAuditLogHandler>('com.enonic.xp.lib.audit.CreateAuditLogHandler');
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
    required(params, 'id');

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

    setIds(ids?: string[] | null): void;

    setFrom(from?: string | null): void;

    setTo(to?: string | null): void;

    setType(type?: string | null): void;

    setSource(source?: string | null): void;

    setUsers(users?: string[] | null): void;

    setObjectUris(objectUris?: string[] | null): void;

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