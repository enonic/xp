var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/assert');

var stream = ioLib.newStream('hello');

// BEGIN
// Returns the size of a stream.
var size = ioLib.getSize(stream);
log.info('Stream size is %s bytes', size);
// END

assert.assertEquals(5, size);
