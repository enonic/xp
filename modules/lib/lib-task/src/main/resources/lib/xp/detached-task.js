/**
 * Internal runner for detached task functions: on pooled script engines `taskLib.executeFunction`
 * always executes the submitted function through this runner, in a fresh script context.
 *
 * Re-materializes the submitted function from its source, so the task can execute on any context
 * instead of being routed back to the submitting one. Variables captured from the submitting
 * scope are NOT available — referencing one throws a ReferenceError. The function gets the
 * eagerly passed `params`, true globals (app, ...) and this runner's module environment:
 * `log`, `require`, `resolve` and `__` — so it can load libraries and talk to the platform.
 * `require`/`resolve` resolve relative to this runner's location — use absolute paths.
 */
exports.run = function (source, params) {
    'use strict';
    var fn;
    try {
        // eslint-disable-next-line no-eval -- indirect eval in global scope isolates the function from the submitting scope
        fn = (0, eval)('(function (log, require, resolve, __) { return (' + source + '); })')(log, require, resolve, __);
    } catch (e) {
        // shorthand method syntax (`func() {...}`) is not a valid expression: evaluate it as an object-literal method
        // eslint-disable-next-line no-eval
        var holder = (0, eval)('(function (log, require, resolve, __) { return ({' + source + '}); })')(log, require, resolve, __);
        for (var key in holder) {
            fn = holder[key];
            break;
        }
    }
    return params === undefined || params === null ? fn() : fn(params);
};
