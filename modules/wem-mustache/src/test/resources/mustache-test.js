var assert = Java.type('org.junit.Assert');
var mustache = require('view/mustache');
var view = resolve('view/test.html');

var html = mustache.render(view, {
    name: 'Steve'
});

assert.assertEquals('<div>Hello Steve!</div>', html);
