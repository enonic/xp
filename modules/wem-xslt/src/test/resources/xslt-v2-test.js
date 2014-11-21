exports.render_no_view = function () {

    return execute2('xslt.render', {});

};

exports.render = function () {

    var view = resolve('view/test-v2.xsl');
    return execute2('xslt.render', {
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
