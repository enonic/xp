var assert = Java.type('org.junit.Assert');

var relative = resolve('./test.html').toString();
assert.assertEquals('mymodule:/resolve/test.html', relative);

var absolute = resolve('/test.html').toString();
assert.assertEquals('mymodule:/test.html', absolute);
