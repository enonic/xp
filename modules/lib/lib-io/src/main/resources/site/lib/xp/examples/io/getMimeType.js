var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns mime-type for a file name.
var type = ioLib.getMimeType('myfile.txt');
log.info('Mime type is %s', type);
// END

assert.assertEquals('text/plain', type);
