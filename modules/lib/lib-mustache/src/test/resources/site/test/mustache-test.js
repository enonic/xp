var mustache = require('/lib/xp/mustache');

function assertHtmlEquals(res, actual) {
    testInstance.assertHtmlEquals(resolve(res), actual);
}

exports.testRender = function () {
    var view = resolve('view/test.html');
    var result = mustache.render(view, {
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

    assertHtmlEquals('view/test-result.html', result);
};

exports.testExamples = function () {
    testInstance.runScript('/site/lib/xp/examples/mustache/render.js')
};
