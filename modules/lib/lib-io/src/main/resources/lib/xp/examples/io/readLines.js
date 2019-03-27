var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/testing');

var stream = ioLib.newStream('line1\nline2\n');

// BEGIN
// Reads lines from stream.
var lines = ioLib.readLines(stream);
log.info('Num lines: %s', lines.length);
// END

assert.assertEquals(2, lines.length);
