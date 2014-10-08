var assert = Java.type('org.junit.Assert');
var view = resolve('view/test.xsl');

var html = execute('view.renderXslt', {
    view: view,
    inputXml: '<input/>',
    parameters: {}
});

assert.assertEquals('<div>Hello</div>', html);
