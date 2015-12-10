var xslt = require('/lib/xp/xslt');

function assertXmlEquals(res, actual) {
    testInstance.assertXmlEquals(resolve(res), actual);
}

exports.testRender = function () {
    var view = resolve('./view/simple.xsl');
    var result = xslt.render(view, {
            fruits: [
                {
                    name: 'Apple',
                    color: 'Red'
                },
                {
                    name: 'Pear',
                    color: 'Green'
                }
            ]
        }
    );

    assertXmlEquals('view/simple-result.xml', result);
};

exports.testUrlFunctions = function () {
    var view = resolve('./view/url-functions.xsl');
    var result = xslt.render(view, {});

    assertXmlEquals('view/url-functions-result.xml', result);
};

exports.testExamples = function () {
    testInstance.runScript('/site/lib/xp/examples/xslt/render.js')
};
