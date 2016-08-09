var thymeleaf = require('/lib/xp/thymeleaf');

function assertHtmlEquals(res, actual) {
    testInstance.assertHtmlEquals(resolve(res), actual);
}

exports.testRender = function () {
    var view = resolve('view/test.html');
    var result = thymeleaf.render(view, {
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

exports.testFunctions = function () {
    var view = resolve('view/functions.html');
    var result = thymeleaf.render(view, {});

    assertHtmlEquals('view/functions-result.html', result);
};

exports.testInlineFragment = function () {
    var view = resolve('fragment/inline-fragment.html');
    var result = thymeleaf.render(view, {});

    assertHtmlEquals('fragment/inline-fragment-result.html', result);
};

exports.testExternalFragment = function () {
    var view = resolve('fragment/external-fragment.html');
    var result = thymeleaf.render(view, {});

    assertHtmlEquals('fragment/external-fragment-result.html', result);
};

exports.testDate = function () {
    var view = resolve('view/date.html');
    var result = thymeleaf.render(view, {
        date: new Date(Date.parse('1995-11-12T22:24:25Z'))
    });

    assertHtmlEquals('view/date-result.html', result);
};

exports.testJsExec = function () {
    var view = resolve('view/jsexec.html');
    var result = thymeleaf.render(view, {
        func1: function () {
            return "Hello";
        },
        func2: function (arg1) {
            return "Hello " + arg1;
        },
        func3: function (arg1, arg2) {
            return "Hello " + arg1 + " and " + arg2;
        }
    });

    assertHtmlEquals('view/jsexec-result.html', result);
};

exports.testExamples = function () {
    testInstance.runScript('/site/lib/xp/examples/thymeleaf/render.js')
};
