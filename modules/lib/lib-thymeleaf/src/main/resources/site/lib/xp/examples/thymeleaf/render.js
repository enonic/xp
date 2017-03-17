var thymeleafLib = require('/lib/xp/thymeleaf');
var assert = require('/lib/xp/assert');

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

var result = thymeleafLib.render(view, model);
// END

assert.assertNotNull(result);

// BEGIN
// Render using options.
result = thymeleafLib.render(view, model, {
    mode: 'html'
});
// END

assert.assertNotNull(result);
