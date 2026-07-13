/**
 * Internal runner for detached task functions (`taskLib.executeFunction` with `detached: true`).
 *
 * Re-materializes the submitted function from its source in this script context, so the task can
 * execute on any pooled context instead of being routed back to the submitting one. Variables
 * captured from the submitting scope are NOT available — referencing one throws a ReferenceError.
 * Only the eagerly passed `params` and true globals (app, log, ...) are in scope.
 */
exports.run = function (source, params) {
    'use strict';
    // Re-materializing the submitted function from source is the feature; indirect eval runs in
    // global scope, which is what isolates the function from the submitting scope.
    // eslint-disable-next-line
    var fn = (0, eval)('(' + source + ')'); // nosemgrep
    return params === undefined || params === null ? fn() : fn(params);
};
