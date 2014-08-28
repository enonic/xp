/**
 * @module view/thymeleaf
 */

/**
 * Render using thymeleaf template.
 *
 * @param {Object} template resolved template
 * @param {Object} params xslt parameters
 */
exports.render = function (template, params) {
    var helper = __('thymeleafScriptHelper');

    var request = helper.newRenderParams();
    request.view(template);
    request.parameters(params);

    return helper.processor.render(request);
};

