var thymeleaf = require('/lib/xp/thymeleaf.js');

exports.renderTest = function () {

    var view = resolve('view/test.html');
    return thymeleaf.render(view, {
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

};

exports.functionsTest = function () {

    var view = resolve('view/functions.html');
    return thymeleaf.render(view, {});

};

exports.inlineFragmentTest = function () {

    var view = resolve('fragment/inline-fragment.html');
    return thymeleaf.render(view, {});

};

exports.externalFragmentTest = function () {

    var view = resolve('fragment/external-fragment.html');
    return thymeleaf.render(view, {});

};
