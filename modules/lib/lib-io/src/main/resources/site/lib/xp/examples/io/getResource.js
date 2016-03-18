var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns a file by name.
var res1 = ioLib.getResource('/site/lib/xp/examples/io/sample.txt');
var exists = res1.exists();
var size = res1.getSize();
var stream = res1.getStream();
// END

// BEGIN
// Returns a file by reference.
var res2 = ioLib.getResource(resolve('./sample.txt'));
if (res2.exists()) {
    log.info('Resource exists');
}
// END

assert.assertEquals(true, exists);
assert.assertEquals(11, size);
assert.assertNotNull(stream);
assert.assertEquals(true, res2.exists());
assert.assertEquals(11, res2.getSize());
assert.assertNotNull(res2.getStream());