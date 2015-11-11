var xslt = require('/lib/xp/xslt.js');

exports.simple = function () {

    var view = resolve('./view/simple.xsl');
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

exports.urlFunctions = function () {

    var view = resolve('./view/url-functions.xsl');
    return xslt.render(view, {});

};
