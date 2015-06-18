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

exports.fragmentsTest = function () {

    var view = resolve('fragment/fragment-main.html');
    return thymeleaf.render(view, {});

};
