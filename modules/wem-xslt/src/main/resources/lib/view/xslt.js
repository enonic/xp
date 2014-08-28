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
    var processor = __('xsltProcessor');

    var request = new com.enonic.wem.xslt.XsltRenderParams();
    request.view(xslt);
    request.inputXml(doc);
    request.parameters(params);

    return processor.render(request);
};
