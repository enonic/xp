/**
 * Functions for execution of asynchronous tasks.
 *
 * @example
 * var taskLib = require('/lib/xp/task');
 *
 * @module task
 */

declare global {
    interface XpLibraries {
        '/lib/xp/task': typeof import('./task');
    }
}

import type {ScriptValue, UserKey} from '@enonic-types/core';

export type {ScriptValue, UserKey} from '@enonic-types/core';

function checkRequired<T extends object>(obj: T, name: keyof T): void {
    if (obj == null || obj[name] == null) {
        throw Error(`Parameter '${String(name)}' is required`);
    }
}

export type CallbackFn = () => void;

export interface ExecuteFunctionParams {
    description: string;
    func: CallbackFn;
}

interface ExecuteFunctionHandler {
    setDescription(value?: string | null): void;

    setFunc(callbackFn?: CallbackFn | null): void;

    executeFunction(): string;
}

/**
 * @typedef TaskInfo
 * @type Object
 * @property {string} id Task ID.
 * @property {string} name Task name.
 * @property {string} description Task description.
 * @property {string} state Task state. Possible values: 'WAITING' | 'RUNNING' | 'FINISHED' | 'FAILED'
 * @property {string} application Application containing the callback function to run.
 * @property {string} user Key of the user that submitted the task.
 * @property {string} startTime Time when the task was submitted (in ISO-8601 format).
 * @property {object} progress Progress information provided by the running task.
 * @property {number} progress.current Latest progress current numeric value.
 * @property {number} progress.total Latest progress target numeric value.
 * @property {string} progress.info Latest progress textual information.
 */


/**
 * Runs a task function in the background and returns an id representing the task.
 *
 * This function returns immediately. The callback function will be executed asynchronously.
 *
 * @example-ref examples/task/executeFunction.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.description Text describing the task to be executed.
 * @param {function} params.func Callback function to be executed asynchronously.
 *
 * @returns {string} Id of the task that will be executed.
 */
export function executeFunction(params: ExecuteFunctionParams): string {
    const bean: ExecuteFunctionHandler = __.newBean<ExecuteFunctionHandler>('com.enonic.xp.lib.task.ExecuteFunctionHandler');

    checkRequired(params, 'description');
    checkRequired(params, 'func');

    bean.setDescription(__.nullOrValue(params.description));
    bean.setFunc(__.nullOrValue(params.func));

    return bean.executeFunction();
}

interface SubmitTaskHandler {
    setDescriptor(value?: string | null): void;

    setName(value?: string | null): void;

    setConfig(value?: ScriptValue): void;

    submitTask(): string;
}


export interface SubmitTaskParams<Config extends Record<string, unknown>> {
    descriptor: string;
    name?: string;
    config?: Config;
}

/**
 * Submits a task to be executed in the background and returns an id representing the task.
 *
 * This function returns immediately. The callback function will be executed asynchronously.
 *
 * @example-ref examples/task/submitTask.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.descriptor Descriptor of the task to execute.
 * @param {string} [params.name] Optional name of the task. If not specified, descriptor name will be used instead.
 * @param {object} [params.config] Configuration parameters to pass to the task to be executed.
 * The object must be valid according to the schema defined in the form of the task descriptor XML.
 * @returns {string} Id of the task that will be executed.
 */
export function submitTask<Config extends Record<string, unknown> = Record<string, unknown>>(params: SubmitTaskParams<Config>): string {
    checkRequired(params, 'descriptor');

    const bean: SubmitTaskHandler = __.newBean<SubmitTaskHandler>('com.enonic.xp.lib.task.SubmitTaskHandler');

    bean.setDescriptor(__.nullOrValue(params.descriptor));
    bean.setName(__.nullOrValue(params.name));
    bean.setConfig(__.toScriptValue(params.config));

    return bean.submitTask();
}

export type TaskStateType = 'WAITING' | 'RUNNING' | 'FINISHED' | 'FAILED';

export interface ListTasksParams {
    name?: string | null;
    state?: TaskStateType | null;
}

interface ListTasksHandler {
    setName(value?: string | null): void;

    setState(value?: TaskStateType | null): void;

    list(): TaskInfo[];
}

export interface TaskProgress {
    info: string;
    current: number;
    total: number;
}

export interface TaskInfo {
    description: string;
    id: string;
    name: string;
    state: TaskStateType;
    application: string;
    user: UserKey;
    startTime: string;
    progress: TaskProgress;
    node: string;
}

/**
 * Returns the list of active tasks with their current state and progress details.
 *
 * @example-ref examples/task/list.js
 *
 * @param {object} [params] JSON with optional parameters.
 * @param {string} [params.name] Filter by name.
 * @param {object} [params.state] Filter by task state ('WAITING' | 'RUNNING' | 'FINISHED' | 'FAILED').
 * @returns {TaskInfo[]} List with task information for every task.
 */
export function list(params?: ListTasksParams): TaskInfo[] {
    const {name, state} = params ?? {};

    const bean: ListTasksHandler = __.newBean<ListTasksHandler>('com.enonic.xp.lib.task.ListTasksHandler');

    bean.setName(__.nullOrValue(name));
    bean.setState(__.nullOrValue(state));

    return __.toNativeObject(bean.list());

}

interface GetTaskHandler {
    setTaskId(value?: string | null): void;

    getTask(): TaskInfo | null;
}

/**
 * Returns the current state and progress details for the specified task.
 *
 * @example-ref examples/task/get.js
 *
 * @param {string} taskId Id of the task.
 *
 * @returns {TaskInfo} Detail information for the task. Or null if the task could not be found.
 */
export function get(taskId: string): TaskInfo | null {
    if (taskId === undefined) {
        throw Error('Parameter "taskId" is required');
    }

    const bean: GetTaskHandler = __.newBean<GetTaskHandler>('com.enonic.xp.lib.task.GetTaskHandler');
    bean.setTaskId(__.nullOrValue(taskId));
    return __.toNativeObject(bean.getTask());
}

interface SleepHandler {
    setTimeMillis(value: number): void;

    sleep(): void;
}

/**
 * Causes the current execution thread to sleep (temporarily cease execution) for the specified number of milliseconds.
 *
 * @example-ref examples/task/sleep.js
 *
 * @param {number} timeMillis The length of time to sleep in milliseconds.
 */
export function sleep(timeMillis: number): void {
    const bean: SleepHandler = __.newBean<SleepHandler>('com.enonic.xp.lib.task.SleepHandler');

    bean.setTimeMillis(__.nullOrValue(timeMillis) ?? 0);

    bean.sleep();
}

export interface TaskProgressParams {
    current?: number | null;
    total?: number | null;
    info?: string | null;
}

interface TaskProgressHandler {
    setCurrent(value?: number | null): void;

    setTotal(value?: number | null): void;

    setInfo(value?: string | null): void;

    reportProgress(): void;
}

/**
 * Reports progress information from an executing task.
 * This function may only be called within the context of a task function, otherwise it will fail and throw an exception.
 *
 * @example-ref examples/task/progress.js
 *
 * @param {object} params JSON with progress details.
 * @param {number} [params.current] Integer value representing the number of items that have been processed in the task.
 * @param {number} [params.total] Integer value representing the total number of items to process in the task.
 * @param {string} [params.info] Text describing the current progress for the task.
 */
export function progress(params: TaskProgressParams): void {
    const bean: TaskProgressHandler = __.newBean<TaskProgressHandler>('com.enonic.xp.lib.task.TaskProgressHandler');

    bean.setCurrent(__.nullOrValue(params.current));
    bean.setTotal(__.nullOrValue(params.total));
    bean.setInfo(__.nullOrValue(params.info));

    bean.reportProgress();
}

interface IsRunningHandler {
    isRunning(taskNameOrId: string): boolean;
}

/**
 * Checks if any task with the given name or id is currently running.
 *
 * @example-ref examples/task/isRunning.js
 *
 * @param {string} task Name or id of the task.
 *
 * @returns {boolean} True if there is a task with the specified name or id, and state 'RUNNING'; False otherwise.
 */
export function isRunning(task: string): boolean {
    const bean: IsRunningHandler = __.newBean<IsRunningHandler>('com.enonic.xp.lib.task.IsRunningHandler');

    if (task === undefined) {
        throw Error('Parameter "task" is required');
    }

    return __.toNativeObject(bean.isRunning(task));
}
