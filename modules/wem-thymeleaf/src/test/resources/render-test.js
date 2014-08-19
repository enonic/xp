var thymeleaf = require('view/thymeleaf');
var view = resolve('view/test.html');

var html = thymeleaf.render(view, {});
test.assertEquals('<html></html>', html);
