var ioLib = require('/lib/xp/io');
var t = require('/lib/xp/testing');

var stream = ioLib.newStream('hello');

// BEGIN
// Returns the size of a stream.
var size = ioLib.getSize(stream);
log.info('Stream size is %s bytes', size);
// END

t.assertEquals(5, size);
