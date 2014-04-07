var a = require('require/absolute/a');
var b = require('require/absolute/b');

test.assert(a.foo().foo === b.foo, 'require works with absolute identifiers');
test.print('DONE');
