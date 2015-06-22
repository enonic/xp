var mustache = require('/lib/xp/mustache.js');

exports.render = function () {

    var view = resolve('./view/test-view.html');
    return mustache.render(view, {
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
