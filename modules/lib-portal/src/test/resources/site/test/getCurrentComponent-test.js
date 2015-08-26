var assert = Java.type('org.junit.Assert');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "name": "mylayout",
    "path": "main/-1",
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
                    "path": "main/-1/bottom/0",
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

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

exports.currentComponent = function () {
    var result = portal.getComponent();
    assertJson(expectedJson, result);
};

exports.noCurrentComponent = function () {
    var result = portal.getComponent();
    assert.assertNull(result);
};
