/**
 * @module view/xslt
 */

/**
 * Render using xslt stylesheet.
 *
 * @param {Object} xslt resolved xslt stylesheet
 * @param {String} doc xml input document
 * @param {Object} params xslt parameters
 */
exports.render = function (xslt, doc, params) {
    var factory = __('xsltProcessorFactory');

    var processor = factory.newProcessor();
    processor.view(xslt);
    processor.inputXml(doc);
    processor.parameters(params);

    return processor.process();
};
