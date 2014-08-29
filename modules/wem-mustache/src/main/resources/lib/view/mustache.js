/**
 * @module view/mustache
 */

/**
 * Render using mustache template.
 *
 * @param {Object} template resolved template
 * @param {Object} params mustache parameters
 */
exports.render = function (template, params) {
    var factory = __('mustacheProcessorFactory');

    var processor = factory.newProcessor();
    processor.view(template);
    processor.parameters(params);

    return processor.process();
};
