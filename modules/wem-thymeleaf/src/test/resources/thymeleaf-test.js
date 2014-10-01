var assert = Java.type('org.junit.Assert');
var view = resolve('view/test.html');

var html = executeCommand('com.enonic.wem.thymeleaf.RenderView', {
    view: view,
    parameters: {}
});

assert.assertEquals('<div>\n    <div><!--# COMPONENT test --></div>\n</div>', html);
