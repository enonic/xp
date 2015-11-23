var assert = require('/lib/xp/assert');
var io = require('/lib/xp/io');
var byteSource = Java.type('com.google.common.io.ByteSource');

function newStream(value) {
    return byteSource.wrap(value.bytes);
}

exports.testReadText = function () {
    var text = io.readText('value');
    assert.assertEquals('value', text);

    text = io.readText(newStream('value'));
    assert.assertEquals('value', text);
};

exports.testReadLines = function () {
    var lines = io.readLines('line1\nline2\n');
    assert.assertJsonEquals([
        "line1",
        "line2"
    ], lines);

    lines = io.readLines(newStream('line1\nline2\n'));
    assert.assertJsonEquals([
        "line1",
        "line2"
    ], lines);
};

exports.testProcessLines = function () {
    var result = '';
    io.processLines('line1\nline2\n', function (line) {
        result = result + '-' + line;
    });

    assert.assertEquals('-line1-line2', result);

    result = '';
    io.processLines(newStream('line1\nline2\n'), function (line) {
        result = result + '-' + line;
    });

    assert.assertEquals('-line1-line2', result);
};

exports.testGetSize = function () {
    var size = io.getSize('value');
    assert.assertEquals(5, size);

    size = io.getSize(newStream('value'));
    assert.assertEquals(5, size);
};

exports.testGetMimeType = function () {
    var type = io.getMimeType('sample.txt');
    assert.assertEquals('text/plain', type);
};

exports.testGetResource = function () {
    var res = io.getResource('/site/test/sample.txt');
    assert.assertEquals('/site/test/sample.txt', res.getPath());
    assert.assertEquals(true, res.exists());
    assert.assertEquals(12, res.getSize());
    assert.assertEquals('sample text', io.readText(res.getStream()).trim());

    res = io.getResource(resolve('./sample.txt'));
    assert.assertEquals('/site/test/sample.txt', res.getPath());
    assert.assertEquals(true, res.exists());
    assert.assertEquals(12, res.getSize());
    assert.assertEquals('sample text', io.readText(res.getStream()).trim());

    res = io.getResource('/unknown.txt');
    assert.assertEquals('/unknown.txt', res.getPath());
    assert.assertEquals(false, res.exists());
};
