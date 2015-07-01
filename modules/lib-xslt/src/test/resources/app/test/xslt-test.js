var xslt = require('/lib/xp/xslt.js');

exports.render = function () {

    var view = resolve('./view/test.xsl');
    return xslt.render(view, {
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
