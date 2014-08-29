var mustache = require('view/mustache');
var view = resolve('view/test.html');

var html = mustache.render(view, {
    name: 'Steve'
});

test.assertEquals('<div>Hello Steve!</div>', html);
