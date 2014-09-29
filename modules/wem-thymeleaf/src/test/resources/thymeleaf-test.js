var assert = Java.type('org.junit.Assert');
var thymeleaf = require('view/thymeleaf');
var view = resolve('view/test.html');

var html = thymeleaf.render(view, {});

assert.assertEquals('<div>\n    <div><!--# COMPONENT test --></div>\n</div>', html);
