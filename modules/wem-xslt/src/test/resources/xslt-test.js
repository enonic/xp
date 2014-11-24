exports.render_no_view = function () {

    return execute('xslt.render', {});

};

exports.render = function () {

    var view = resolve('view/test.xsl');
    return execute('xslt.render', {
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
