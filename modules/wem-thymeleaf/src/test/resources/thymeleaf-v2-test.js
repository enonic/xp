exports.render_no_view = function () {

    return execute2('thymeleaf.render', {});

};

exports.render = function () {

    var view = resolve('view/test-v2.html');
    return execute2('thymeleaf.render', {
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
