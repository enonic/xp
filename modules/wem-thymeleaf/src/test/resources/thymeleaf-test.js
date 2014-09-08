var assert = require('assert');
var thymeleaf = require('view/thymeleaf');
var view = resolve('view/test.html');

var html = thymeleaf.render(view, {});

assert.assertEquals('<div>\n    <div><!--# COMPONENT test --></div>\n</div>', html);
