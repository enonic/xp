var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "name": "mylayout",
    "path": "main/0",
    "type": "layout",
    "descriptor": "myapplication:mylayout",
    "config": {
        "a": "1"
    },
    "regions": {
        "bottom": {
            "components": [
                {
                    "name": "mypart",
                    "path": "main/0/bottom/0",
                    "type": "part",
                    "descriptor": "myapplication:mypart",
                    "config": {
                        "a": "1"
                    }
                }
            ],
            "name": "bottom"
        }
    }
};

exports.currentComponent = function () {
    var result = portal.getComponent();
    assert.assertJsonEquals(expectedJson, result);
};

exports.noCurrentComponent = function () {
    var result = portal.getComponent();
    assert.assertNull(result);
};
