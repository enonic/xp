var a = require('/absolute/a');
var b = require('/absolute/b');

test.assertTrue(a.foo().foo === b.foo, 'require works with absolute identifiers');
