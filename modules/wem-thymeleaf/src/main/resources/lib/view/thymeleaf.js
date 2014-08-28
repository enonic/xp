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
    var processor = __('thymeleafProcessor');

    var request = new com.enonic.wem.thymeleaf.ThymeleafRenderParams();
    request.view(template);
    request.parameters(params);

    return processor.render(request);
};

