var ioLib = require('/lib/xp/io');
var assert = require('/lib/xp/testing');

var stream = ioLib.newStream('line1\nline2\n');

// BEGIN
var num = 0;

// Process lines from stream.
ioLib.processLines(stream, function (line) {
    num++;
    log.info('Line %s: %s', num, line);
});
// END

assert.assertEquals(2, num);
