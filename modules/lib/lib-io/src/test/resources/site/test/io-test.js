var t = require('/lib/xp/testing');
var io = require('/lib/xp/io');

exports.testReadText = function () {
    var text = io.readText(io.newStream('value'));
    t.assertEquals('value', text);
};

exports.testReadLines = function () {
    var lines = io.readLines(io.newStream('line1\nline2\n'));
    t.assertJsonEquals([
        "line1",
        "line2"
    ], lines);
};

exports.testProcessLines = function () {
    var result = '';
    io.processLines(io.newStream('line1\nline2\n'), function (line) {
        result = result + '-' + line;
    });

    t.assertEquals('-line1-line2', result);
};

exports.testGetSize = function () {
    var size = io.getSize(io.newStream('value'));
    t.assertEquals(5, size);
};

exports.testGetMimeType = function () {
    var type = io.getMimeType('sample.txt');
    t.assertEquals('text/plain', type);
};

exports.testGetResource = function () {
    var res = io.getResource('/site/test/sample.txt');
    t.assertEquals(true, res.exists());
    t.assertEquals(11, res.getSize());
    t.assertEquals('sample text', io.readText(res.getStream()).trim());

    res = io.getResource(resolve('./sample.txt'));
    t.assertEquals(true, res.exists());
    t.assertEquals(11, res.getSize());
    t.assertEquals('sample text', io.readText(res.getStream()).trim());

    res = io.getResource('/unknown.txt');
    t.assertEquals(false, res.exists());
};

function runExample(name) {
    t.runScript('/site/lib/xp/examples/io/' + name + '.js');
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
