/**
 * Mustache template related functions.
 *
 * @example
 * var mustacheLib = require('/lib/xp/mustache');
 *
 * @module lib/xp/mustache
 */

var service = __.newBean('com.enonic.xp.lib.mustache.MustacheService');

/**
 * This function renders a view using mustache.
 *
 * @example
 * var view = resolve('view/fruit.html');
 * var model = {
 * fruits: [
 *   {
 *     name: 'Apple',
 *     color: 'Red'
 *   },
 *   {
 *     name: 'Pear',
 *     color: 'Green'
 *   }
 * ]};
 *
 * var mustacheLib = require('/lib/xp/mustache');
 * var result = mustacheLib.render(view, model);
 *
 * @param view Location of the view. Use `resolve(..)` to resolve a view.
 * @param {object} model Model that is passed to the view.
 * @returns {string} The rendered output.
 */
exports.render = function (view, model) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = __.toScriptValue(model);
    return processor.process();
};
