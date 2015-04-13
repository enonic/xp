exports.noViewTest = function () {

    return execute('thymeleaf.render', {});

};

exports.renderTest = function () {

    var view = resolve('view/test.html');
    return execute('thymeleaf.render', {
        view: view,
        model: {
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
    });

};

exports.functionsTest = function () {

    var view = resolve('view/functions.html');
    return execute('thymeleaf.render', {
        view: view,
        model: {}
    });

};

exports.fragmentsTest = function () {

    var view = resolve('fragment/fragment-main.html');
    return execute('thymeleaf.render', {
        view: view,
        model: {}
    });

};
