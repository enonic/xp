/**
 * @module view/thymeleaf
 */

/**
 * Render using thymeleaf template.
 *
 * @param {Object} template resolved template
 * @param {Object} params thymeleaf parameters
 */
exports.render = function (template, params) {
    var factory = __('thymeleafProcessorFactory');

    var processor = factory.newProcessor();
    processor.view(template);
    processor.parameters(params);

    return processor.process();
};
