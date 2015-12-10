var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/assert');

// BEGIN
// Creates a new stream from a string.
var stream = ioLib.newStream('Hello World');
// END

assert.assertEquals('Hello World', ioLib.readText(stream));
