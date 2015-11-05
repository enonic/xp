/**
 * XSLT template related functions.
 *
 * @example
 * var authLib = require('/lib/xp/xslt');
 *
 * @module lib/xp/xslt
 */

var service = __.newBean('com.enonic.xp.lib.xslt.XsltService');

exports.render = function (view, model) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = __.toScriptValue(model);
    return processor.process();
};
