var assert = require('/lib/xp/assert.js');
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
    assert.assertJsonEquals('Component JSON not equals', expectedJson, result);
};

exports.noCurrentComponent = function () {
    var result = portal.getComponent();
    assert.assertNull('Component JSON not null', result);
};
