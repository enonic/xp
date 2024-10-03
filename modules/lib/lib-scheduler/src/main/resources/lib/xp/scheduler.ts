/**
 * Scheduler functions.
 *
 * @example
 * var schedulerLib = require('/lib/xp/scheduler');
 *
 * @module scheduler
 */

declare global {
    interface XpLibraries {
        '/lib/xp/scheduler': typeof import('./scheduler');
    }
}

import type {ScriptValue, UserKey} from '@enonic-types/core';

export type {PrincipalKey, UserKey, GroupKey, RoleKey, ScriptValue} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === undefined) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export type EditorFn<T> = (value: T) => T;

export interface CreateScheduledJobParams<Config extends Record<string, unknown>> {
    name: string;
    description?: string;
    descriptor: string;
    config?: Config;
    schedule: OneTimeSchedule | CronSchedule;
    user?: UserKey;
    enabled: boolean;
}

export interface OneTimeSchedule {
    type: 'ONE_TIME';
    value: string;
}

export interface CronSchedule {
    type: 'CRON';
    value: string;
    timeZone: string;
}

export interface ScheduledJob<Config extends Record<string, unknown> = Record<string, unknown>> {
    name: string;
    descriptor: string;
    description?: string | null;
    enabled: boolean;
    config?: Config | null;
    user?: UserKey | null;
    creator: UserKey;
    modifier: UserKey;
    createdTime: string;
    modifiedTime: string;
    lastRun?: string | null;
    lastTaskId?: string | null;
    schedule: OneTimeSchedule | CronSchedule;
}

interface CreateScheduledJobHandler<Config extends Record<string, unknown>> {
    setName(value: string): void;

    setSchedule(value: OneTimeSchedule | CronSchedule): void;

    setDescriptor(value: string): void;

    setEnabled(value: boolean): void;

    setDescription(value?: string | null): void;

    setConfig(value?: object | null): void;

    setUser(value?: UserKey | null): void;

    execute(): ScheduledJob<Config>;
}

/**
 * Creates a job to be scheduled.
 *
 * @example-ref examples/scheduler/create.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name unique job name.
 * @param {string} [params.description] job description.
 * @param {string} params.descriptor descriptor of the task to be scheduled.
 * @param {object} [params.config] config of the task to be scheduled.
 * @param {object} params.schedule task time run config.
 * @param {string} params.schedule.value schedule value according to its type.
 * @param {string} params.schedule.type schedule type (CRON | ONE_TIME).
 * @param {string} params.schedule.timezone time zone of cron scheduling.
 * @param {string} [params.user] key of the user that submitted the task.
 * @param {boolean} params.enabled job is active or not.
 */
export function create<Config extends Record<string, unknown> = Record<string, unknown>>(params: CreateScheduledJobParams<Config>): ScheduledJob<Config> {
    checkRequired(params, 'name');
    checkRequired(params, 'schedule');
    checkRequired(params, 'descriptor');
    checkRequired(params, 'enabled');

    const bean: CreateScheduledJobHandler<Config> = __.newBean<CreateScheduledJobHandler<Config>>('com.enonic.xp.lib.scheduler.CreateScheduledJobHandler');

    bean.setName(params.name);
    bean.setSchedule(params.schedule);
    bean.setDescriptor(params.descriptor);
    bean.setEnabled(params.enabled);
    bean.setDescription(__.nullOrValue(params.description));
    bean.setConfig(__.toScriptValue(params.config));
    bean.setUser(__.nullOrValue(params.user));

    return __.toNativeObject(bean.execute());
}

export interface ModifyScheduledJobParams<Config extends Record<string, unknown>> {
    name: string;
    editor: EditorFn<ScheduledJob<Config>>;
}

interface ModifyScheduledJobHandler<Config extends Record<string, unknown>> {
    setName(value: string): void;

    setEditor(value: ScriptValue): void;

    execute(): ScheduledJob<Config>;
}

/**
 * Modifies scheduled job.
 *
 * @example-ref examples/scheduler/modify.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name unique job name.
 * @param {function} params.editor editor callback function, has editable existing job as a param.
 */
export function modify<Config extends Record<string, unknown> = Record<string, unknown>>(params: ModifyScheduledJobParams<Config>): ScheduledJob<Config> {
    checkRequired(params, 'name');

    const bean: ModifyScheduledJobHandler<Config> = __.newBean<ModifyScheduledJobHandler<Config>>('com.enonic.xp.lib.scheduler.ModifyScheduledJobHandler');

    bean.setName(params.name);
    bean.setEditor(__.toScriptValue(params.editor));

    return __.toNativeObject(bean.execute());
}

export interface DeleteScheduledJobParams {
    name: string;
}

interface DeleteScheduledJobHandler {
    setName(value: string): void;

    execute(): boolean;
}

/**
 * Removes scheduled job.
 *
 * @example-ref examples/scheduler/delete.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name job to be deleted name.
 */
function _delete(params: DeleteScheduledJobParams): boolean {
    checkRequired(params, 'name');

    const bean: DeleteScheduledJobHandler = __.newBean<DeleteScheduledJobHandler>('com.enonic.xp.lib.scheduler.DeleteScheduledJobHandler');

    bean.setName(params.name);

    return __.toNativeObject(bean.execute());
}

export {
    _delete as delete,
};

export interface GetScheduledJobParams {
    name: string;
}

interface GetScheduledJobHandler<Config extends Record<string, unknown>> {
    setName(value: string): void;

    execute(): ScheduledJob<Config> | null;
}

/**
 * Fetches scheduled job.
 *
 * @example-ref examples/scheduler/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name job to be fetched name.
 */
export function get<Config extends Record<string, unknown> = Record<string, unknown>>(params: GetScheduledJobParams): ScheduledJob<Config> | null {
    checkRequired(params, 'name');

    const bean: GetScheduledJobHandler<Config> = __.newBean<GetScheduledJobHandler<Config>>('com.enonic.xp.lib.scheduler.GetScheduledJobHandler');

    bean.setName(params.name);

    return __.toNativeObject(bean.execute());
}

interface ListScheduledJobsHandler<Config extends Record<string, unknown>> {
    execute(): ScheduledJob<Config>[];
}

/**
 * Lists scheduled jobs.
 *
 * @example-ref examples/scheduler/list.js
 *
 */
export function list<Config extends Record<string, unknown> = Record<string, unknown>>(): ScheduledJob<Config>[] {
    const bean: ListScheduledJobsHandler<Config> = __.newBean<ListScheduledJobsHandler<Config>>('com.enonic.xp.lib.scheduler.ListScheduledJobsHandler');

    return __.toNativeObject(bean.execute());
}


