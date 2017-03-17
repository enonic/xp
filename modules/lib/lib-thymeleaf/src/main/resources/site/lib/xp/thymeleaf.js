/**
 * Thymeleaf template related functions.
 *
 * @example
 * var thymeleafLib = require('/lib/xp/thymeleaf');
 *
 * @module lib/xp/thymeleaf
 */

var service = __.newBean('com.enonic.xp.lib.thymeleaf.ThymeleafService');

/**
 * This function renders a view using thymeleaf.
 *
 * @example-ref examples/thymeleaf/render.js
 *
 * @param view Location of the view. Use `resolve(..)` to resolve a view.
 * @param {object} model Model that is passed to the view.
 * @param {object} [options] Rendering options (optional).
 * @param {string} [options.mode=HTML] Rendering mode. Valid options are: HTML, XML, TEXT, JAVASCRIPT, CSS and RAW.
 *
 * @returns {string} The rendered output.
 */
exports.render = function (view, model, options) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = __.toScriptValue(model);

    var opts = options || {};
    processor.mode = opts.mode || 'HTML';
    return processor.process();
};
