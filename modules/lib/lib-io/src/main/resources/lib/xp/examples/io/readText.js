var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/testing');

var stream = ioLib.newStream('hello');

// BEGIN
// Reads text from stream.
var text = ioLib.readText(stream);
log.info('Text: %s', text);
// END

assert.assertEquals('hello', text);
