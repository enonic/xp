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

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] === undefined) {
        throw `Parameter '${String(name)}' is required`;
    }
}

export type EditorFn<T> = (value: T) => T;

export type ScheduleType = 'CRON' | 'ONE_TIME';

export interface Schedule {
    value: string;
    type: ScheduleType;
    timezone: string;
}

export interface CreateScheduledJobParams {
    name: string;
    description: string;
    descriptor: string;
    config: Record<string, unknown>;
    schedule: Schedule;
    user: string;
    enabled: boolean;
}

export interface OnTimeSchedule {
    value: string;
    type: ScheduleType;
}

export interface CronSchedule
    extends OnTimeSchedule {
    timeZone: string;
}

export interface ScheduleJob {
    name: string;
    descriptor: string;
    description: string;
    enabled: boolean;
    config?: Record<string, unknown> | null;
    user?: string | null;
    creator?: string | null;
    modifier?: string | null;
    createdTime?: string | null;
    modifiedTime?: string | null;
    lastRun?: string | null;
    lastTaskId?: string | null;
    schedule: OnTimeSchedule | CronSchedule;
}

interface CreateScheduledJobHandler {
    setName(value: string): void;

    setSchedule(value: Schedule): void;

    setDescriptor(value: string): void;

    setEnabled(value: boolean): void;

    setDescription(value?: string | null): void;

    setConfig(value?: object | null): void;

    setUser(value?: string | null): void;

    execute(): ScheduleJob;
}

/**
 * Creates a job to be scheduled.
 *
 * @example-ref examples/scheduler/create.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name unique job name.
 * @param {string} params.description job description.
 * @param {string} params.descriptor descriptor of the task to be scheduled.
 * @param {object} params.config config of the task to be scheduled.
 * @param {object} params.schedule task time run config.
 * @param {string} params.schedule.value schedule value according to it's type.
 * @param {string} params.schedule.type schedule type (CRON | ONE_TIME).
 * @param {string} params.schedule.timezone time zone of cron scheduling.
 * @param {string} params.user key of the user that submitted the task.
 * @param {boolean} params.enabled job is active or not.
 */
export function create(params: CreateScheduledJobParams): ScheduleJob {
    checkRequired(params, 'name');
    checkRequired(params, 'schedule');
    checkRequired(params, 'descriptor');
    checkRequired(params, 'enabled');

    const bean = __.newBean<CreateScheduledJobHandler>('com.enonic.xp.lib.scheduler.CreateScheduledJobHandler');

    bean.setName(params.name);
    bean.setSchedule(params.schedule);
    bean.setDescriptor(params.descriptor);
    bean.setEnabled(params.enabled);
    bean.setDescription(__.nullOrValue(params.description));
    bean.setConfig(__.toScriptValue(params.config));
    bean.setUser(__.nullOrValue(params.user));

    return __.toNativeObject(bean.execute());
}

export interface ModifyScheduledJobParams {
    name: string;
    editor: EditorFn<ScheduleJob>;
}

interface ModifyScheduledJobHandler {
    setName(value: string): void;

    setEditor(value: ScriptValue): void;

    execute(): ScheduleJob;
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
export function modify(params: ModifyScheduledJobParams): ScheduleJob {
    checkRequired(params, 'name');

    const bean = __.newBean<ModifyScheduledJobHandler>('com.enonic.xp.lib.scheduler.ModifyScheduledJobHandler');

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

    const bean = __.newBean<DeleteScheduledJobHandler>('com.enonic.xp.lib.scheduler.DeleteScheduledJobHandler');

    bean.setName(params.name);

    return __.toNativeObject(bean.execute());
}

export {
    _delete as delete,
};

export interface GetScheduledJobParams {
    name: string;
}

interface GetScheduledJobHandler {
    setName(value: string): void;

    execute(): ScheduleJob;
}

/**
 * Fetches scheduled job.
 *
 * @example-ref examples/scheduler/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name job to be deleted name.
 */
export function get(params: GetScheduledJobParams): ScheduleJob {
    checkRequired(params, 'name');

    const bean = __.newBean<GetScheduledJobHandler>('com.enonic.xp.lib.scheduler.GetScheduledJobHandler');

    bean.setName(params.name);

    return __.toNativeObject(bean.execute());
}

interface ListScheduledJobsHandler {
    execute(): ScheduleJob[];
}

/**
 * Lists scheduled jobs.
 *
 * @example-ref examples/scheduler/list.js
 *
 */
export function list(): ScheduleJob[] {
    const bean = __.newBean<ListScheduledJobsHandler>('com.enonic.xp.lib.scheduler.ListScheduledJobsHandler');

    return __.toNativeObject(bean.execute());
}


