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
    return __('thymeleafProcessor').process(template, params);
};

