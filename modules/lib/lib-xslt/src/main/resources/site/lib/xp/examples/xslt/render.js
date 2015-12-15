var xsltLib = require('/lib/xp/xslt');
var assert = require('/lib/xp/assert');

// BEGIN
var view = resolve('view/fruit.xslt');
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

var result = xsltLib.render(view, model);
// END

assert.assertNotNull(result);
