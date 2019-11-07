var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = {"contentIds": ["d898972d-f1eb-40a8-a7f2-16abd4c105da", "9efadb7b-bb14-4c74-82ec-cec95069d0c2"]};

exports.getById = function () {
    var result = content.getOutboundDependencies({
        key: 'contentId'
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getByPath = function () {
    var result = content.getOutboundDependencies({
        key: '/contentPath'
    });

    assert.assertJsonEquals(expectedJson, result);
};