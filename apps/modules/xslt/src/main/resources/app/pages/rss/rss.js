var xslt = require('/lib/xp/xslt');
var view = resolve('view.xsl');

exports.get = function (req) {

    var model = {
        fruits: [
            {
                name: 'Banana',
                link: 'http://en.wikipedia.org/wiki/Banana',
                description: 'A banana is an edible fruit but botanically a berry.'
            },
            {
                name: 'Apple',
                link: 'http://en.wikipedia.org/wiki/Apple',
                description: 'Apples grows on trees in the rose family.'
            },
            {
                name: 'Pear',
                link: 'http://en.wikipedia.org/wiki/Pear',
                description: 'The pear is any of several tree and shrub species of genus Pyrus.'
            }
        ]
    };

    var body = xslt.render(view, model);

    return {
        contentType: 'text/xml',
        body: body
    };
};
