var a = require('./a');
var b = require('./b');

test.assertTrue(a.a != undefined, 'a exists');
test.assertTrue(b.b != undefined, 'b exists')
test.assertTrue(a.a().b === b.b, 'a gets b');
test.assertTrue(b.b().a === a.a, 'b gets a');
