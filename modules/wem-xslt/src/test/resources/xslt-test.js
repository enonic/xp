var xslt = require('view/xslt');
var view = resolve('view/test.xsl');

var html = xslt.render(view, '<input/>', {});

test.assertEquals('<div>Hello</div>', html);
