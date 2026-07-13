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
    var fn;
    try {
        // eslint-disable-next-line no-eval -- re-materializing the submitted function from source is
        // the feature; indirect eval runs in global scope, isolating it from the submitting scope
        fn = (0, eval)('(' + source + ')');
    } catch (e) {
        // shorthand method syntax (`func() {...}`) is not a valid expression on its own:
        // evaluate it as an object-literal method instead
        // eslint-disable-next-line no-eval
        var holder = (0, eval)('({' + source + '})');
        for (var key in holder) {
            fn = holder[key];
            break;
        }
    }
    return params === undefined || params === null ? fn() : fn(params);
};
