var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var result = portalLib.getComponent();
log.info('Current component name = %s', result.name);
// END

// BEGIN
// Component returned.
var expected = {
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
// END

assert.assertJsonEquals(expected, result);
