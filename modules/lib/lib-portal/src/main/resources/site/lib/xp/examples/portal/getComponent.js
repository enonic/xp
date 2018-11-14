var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var result = portalLib.getComponent();
log.info('Current component name = %s', result.name);
// END

// BEGIN
// Component returned.
var expected = {
    "path": "/main/0",
    "type": "layout",
    "descriptor": "myapplication:mylayout",
    "config": {
        "a": "1"
    },
    "regions": {
        "bottom": {
            "components": [
                {
                    "path": "/main/0/bottom/0",
                    "type": "part",
                    "descriptor": "myapplication:mypart",
                    "config": {
                        "a": "1"
                    }
                }
            ]
        }
    }
};
// END

assert.assertJsonEquals(expected, result);
