/**
 * Mustache template related functions.
 *
 * @example
 * var authLib = require('/lib/xp/mustache');
 *
 * @module lib/xp/mustache
 */

var service = __.newBean('com.enonic.xp.lib.mustache.MustacheService');

exports.render = function (view, model) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = __.toScriptValue(model);
    return processor.process();
};
