exports.render = function () {

    var mustache = require('/lib/xp/mustache.js');
    var view = resolve('./test-view.html');

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
