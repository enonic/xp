var assert = require('/lib/xp/assert');
var io = require('/lib/xp/io');

exports.testReadText = function () {
    var text = io.readText(io.newStream('value'));
    assert.assertEquals('value', text);
};

exports.testReadLines = function () {
    var lines = io.readLines(io.newStream('line1\nline2\n'));
    assert.assertJsonEquals([
        "line1",
        "line2"
    ], lines);
};

exports.testProcessLines = function () {
    var result = '';
    io.processLines(io.newStream('line1\nline2\n'), function (line) {
        result = result + '-' + line;
    });

    assert.assertEquals('-line1-line2', result);
};

exports.testGetSize = function () {
    var size = io.getSize(io.newStream('value'));
    assert.assertEquals(5, size);
};

exports.testGetMimeType = function () {
    var type = io.getMimeType('sample.txt');
    assert.assertEquals('text/plain', type);
};

exports.testGetResource = function () {
    var res = io.getResource('/site/test/sample.txt');
    assert.assertEquals(true, res.exists());
    assert.assertEquals(11, res.getSize());
    assert.assertEquals('sample text', io.readText(res.getStream()).trim());

    res = io.getResource(resolve('./sample.txt'));
    assert.assertEquals(true, res.exists());
    assert.assertEquals(11, res.getSize());
    assert.assertEquals('sample text', io.readText(res.getStream()).trim());

    res = io.getResource('/unknown.txt');
    assert.assertEquals(false, res.exists());
};

function runExample(name) {
    testInstance.runScript('/site/lib/xp/examples/io/' + name + '.js');
}

exports.testExamples = function () {
    runExample('getMimeType');
    runExample('getResource');
    runExample('getSize');
    runExample('newStream');
    runExample('processLines');
    runExample('readLines');
    runExample('readText');
};
