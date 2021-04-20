/**
 * Scheduler functions.
 *
 * @example
 * var schedulerLib = require('/lib/xp/scheduler');
 *
 * @module event
 */

/* global __*/

function required(params, name) {
    var value = params[String(name)];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }

    return value;
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
 * @param {object} params.user Key of the user that submitted the task.
 * @param {boolean} params.enabled job is active or not.
 */
exports.create = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.scheduler.CreateScheduledJobHandler');

    bean.name = required(params, 'name');
    bean.schedule = required(params, 'schedule');
    bean.descriptor = required(params, 'descriptor');
    bean.enabled = required(params, 'enabled');
    bean.description = nullOrValue(params.description);
    bean.config = nullOrValue(params.config);
    bean.user = nullOrValue(params.user);

    return __.toNativeObject(bean.execute());
};

/**
 * Modifies scheduled job.
 *
 * @example-ref examples/scheduler/modify.js
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
 * @param {object} params.user Key of the user that submitted the task.
 * @param {boolean} params.enabled job is active or not.
 */
exports.modify = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.scheduler.ModifyScheduledJobHandler');

    bean.name = required(params, 'name');
    bean.editor = __.toScriptValue(params.editor);

    return __.toNativeObject(bean.execute());
};

/**
 * Removes scheduled job.
 *
 * @example-ref examples/scheduler/delete.js
 *
 * @param {object} params JSON with the parameters.
 * @param {boolean} params.name job to be deleted name.
 */
exports.delete = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.scheduler.DeleteScheduledJobHandler');

    bean.name = required(params, 'name');

    return __.toNativeObject(bean.execute());
};

/**
 * Fetches scheduled job.
 *
 * @example-ref examples/scheduler/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {boolean} params.name job to be deleted name.
 */
exports.get = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.scheduler.GetScheduledJobHandler');

    bean.name = required(params, 'name');

    return __.toNativeObject(bean.execute());
};

/**
 * Lists scheduled jobs.
 *
 * @example-ref examples/scheduler/list.js
 *
 */
exports.list = function () {
    var bean = __.newBean('com.enonic.xp.lib.scheduler.ListScheduledJobsHandler');

    return __.toNativeObject(bean.execute());
};


