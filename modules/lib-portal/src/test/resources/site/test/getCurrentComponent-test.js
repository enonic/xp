var assert = Java.type('org.junit.Assert');
var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "config": {
        "a": "1"
    },
    "descriptor": "myapplication:mylayout",
    "name": "mylayout",
    "path": "main/-1",
    "regions": {
        "bottom": {
            "components": [{
                "config": {
                    "a": "1"
                },
                "descriptor": "myapplication:mypart",
                "name": "mypart",
                "path": "main/-1/bottom/0",
                "type": "part"
            }],
            "name": "bottom"
        }
    },
    "type": "layout"
};

exports.currentComponent = function () {
    var result = portal.getComponent();
    scriptAssert.assertJson(expectedJson, result);
};

exports.noCurrentComponent = function () {
    var result = portal.getComponent();
    assert.assertNull(result);
};
