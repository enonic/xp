var assert = Java.type('org.junit.Assert');

var relative = resolve('./test.html').toString();
assert.assertEquals('mymodule-1.0.0:/resolve/test.html', relative);

var absolute = resolve('/test.html').toString();
assert.assertEquals('mymodule-1.0.0:/test.html', absolute);
