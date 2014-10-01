var assert = Java.type('org.junit.Assert');
var view = resolve('view/test.html');

var html = executeCommand('com.enonic.wem.mustache.RenderView', {
    view: view,
    parameters: {
        name: 'Steve'
    }
});

assert.assertEquals('<div>Hello Steve!</div>', html);
