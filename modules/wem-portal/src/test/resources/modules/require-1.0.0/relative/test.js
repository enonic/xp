var a = require('./a');
var b = require('./b');

test.assert(a.foo == b.foo, 'a and b share foo through a relative require');
test.print('DONE');
