var mustacheLib = require('/lib/xp/mustache');
var assert = require('/lib/xp/testing');

// BEGIN
var view = resolve('view/fruit.html');
var model = {
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
};

var result = mustacheLib.render(view, model);
// END

assert.assertNotNull(result);
