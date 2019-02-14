var t = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = [
    "d7ad428b-eae2-4ff1-9427-e8e8a8a3ab23",
    "9f5b0db0-38f9-4e81-b92e-116f25476b1c",
    "e1f57280-d672-4cd8-b674-98e26e5b69ae"
];

exports.unpublishById = function () {
    var result = content.unpublish({
        keys: ['9f5b0db0-38f9-4e81-b92e-116f25476b1c', '45d67001-7f2b-4093-99ae-639be9fdd1f6', '79e21db0-5b43-45ce-b58c-6e1c420b22bd']
    });

    t.assertJsonEquals(expectedJson, result);
};

exports.unpublishByPath = function () {
    var result = content.unpublish({
        keys: ['/myfolder/mycontent', '/yourfolder/yourcontent']
    });

    t.assertJsonEquals(expectedJson, result);
};
