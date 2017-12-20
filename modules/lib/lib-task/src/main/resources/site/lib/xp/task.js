/**
 * Functions for execution of asynchronous tasks.
 *
 * @example
 * var taskLib = require('/lib/xp/task');
 *
 * @module task
 */

function checkRequired(params, name) {
    if (params[name] === undefined) {
        throw "Parameter '" + name + "' is required";
    }
}

/**
 * @typedef TaskInfo
 * @type Object
 * @property {string} id Task Id.
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
 * Submits a task to be executed in the background and returns an id representing the task.
 *
 * This function returns immediately. The callback function will be executed asynchronously.
 *
 * @example-ref examples/task/submit.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.description Text describing the task to be executed.
 * @param {function} params.task Callback function to be executed asynchronously.
 *
 * @returns {string} Id of the task that will be executed.
 */
exports.submit = function (params) {

    var bean = __.newBean('com.enonic.xp.lib.task.SubmitTaskHandler');

    checkRequired(params, 'description');
    checkRequired(params, 'task');

    bean.description = __.nullOrValue(params.description);
    bean.task = __.nullOrValue(params.task);

    return bean.submit();
};

/**
 * Submits a named task to be executed in the background and returns an id representing the task.
 *
 * This function returns immediately. The callback function will be executed asynchronously.
 *
 * @example-ref examples/task/submitNamed.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.name Name of the task to execute.
 * @param {object} [params.config] Configuration parameters to pass to the task to be executed.
 * The object must be valid according to the schema defined in the form of the task descriptor XML.
 * @returns {string} Id of the task that will be executed.
 */
exports.submitNamed = function (params) {
    checkRequired(params, 'name');

    var bean = __.newBean('com.enonic.xp.lib.task.SubmitNamedTaskHandler');

    bean.name = __.nullOrValue(params.name);
    bean.config = __.toScriptValue(params.config);

    return bean.submit();
};

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
exports.list = function (params) {
    params = params || {};
    var bean = __.newBean('com.enonic.xp.lib.task.ListTasksHandler');

    bean.name = __.nullOrValue(params.name);
    bean.state = __.nullOrValue(params.state);

    return __.toNativeObject(bean.list());

};

/**
 * Returns the current state and progress details for the specified task.
 *
 * @example-ref examples/task/get.js
 *
 * @param {string} taskId Id of the task.
 *
 * @returns {TaskInfo} Detail information for the task. Or null if the task could not be found.
 */
exports.get = function (taskId) {

    var bean = __.newBean('com.enonic.xp.lib.task.GetTaskHandler');
    if (taskId === undefined) {
        throw "Parameter taskId is required";
    }

    bean.taskId = __.nullOrValue(taskId);

    return __.toNativeObject(bean.getTask());

};

/**
 * Causes the current execution thread to sleep (temporarily cease execution) for the specified number of milliseconds.
 *
 * @example-ref examples/task/sleep.js
 *
 * @param {string} timeMillis The length of time to sleep in milliseconds.
 */
exports.sleep = function (timeMillis) {

    var bean = __.newBean('com.enonic.xp.lib.task.SleepHandler');

    bean.timeMillis = __.nullOrValue(timeMillis) || 0;

    bean.sleep();

};

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
exports.progress = function (params) {

    var bean = __.newBean('com.enonic.xp.lib.task.TaskProgressHandler');

    bean.current = __.nullOrValue(params.current);
    bean.total = __.nullOrValue(params.total);
    bean.info = __.nullOrValue(params.info);

    bean.reportProgress();

};

/**
 * Checks if any task with the given name or id is currently running.
 *
 * @example-ref examples/task/isRunning.js
 *
 * @param {string} task Name or id of the task.
 *
 * @returns {boolean} True if there is a task with the specified name or id, and state 'RUNNING'; False otherwise.
 */
exports.isRunning = function (task) {

    var bean = __.newBean('com.enonic.xp.lib.task.IsRunningHandler');
    if (task === undefined) {
        throw "Parameter task is required";
    }

    return __.toNativeObject(bean.isRunning(task));

};
